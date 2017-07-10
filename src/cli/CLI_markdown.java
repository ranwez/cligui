package cli;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import cli.exceptions.StoppedProgramException;

final class CLI_markdown
{
	private final CLI_api api;

	private final String markdownFilepath;

	private Element listTag;

	CLI_markdown(final CLI_api api, final String markdownFilepath, final String htmlFilepath) throws Exception
	{
		this.api = api;
		this.markdownFilepath = markdownFilepath;

		final DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

		final DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

		final Document document = documentBuilder.newDocument();

		final BufferedReader bufferedReader = new BufferedReader(new FileReader(markdownFilepath));

		final Element root = document.createElement("html");

		StringBuilder lineBuilder = new StringBuilder();

		String line = bufferedReader.readLine();

		while (line != null)
		{
			if (line.isEmpty() && ! lineBuilder.toString().isEmpty())
			{
				readLine(document, root, lineBuilder.toString());

				lineBuilder = new StringBuilder();
			}
			else
			{
				if (isPuce(line) || isNumerotation(line))
				{
					readLine(document, root, line);

					lineBuilder = new StringBuilder();
				}
				else
				{
					if (! lineBuilder.toString().isEmpty())
					{
						lineBuilder.append(' ');
					}

					lineBuilder.append(line);
				}
			}

			line = bufferedReader.readLine();
		}

		bufferedReader.close();

		readLine(document, root, lineBuilder.toString());

		document.appendChild(root);

		final DOMSource source = new DOMSource(document);

		createTransformer().transform(source, new StreamResult(htmlFilepath));
	}

	private void readLine(final Document document, final Element root, final String line) throws StoppedProgramException
	{
		if (isPuce(line))
		{
			if (listTag == null)
			{
				listTag = document.createElement("ul");
			}

			final Element lineTag = document.createElement("li");

			tokenize(document, lineTag, line.substring(2));

			listTag.appendChild(lineTag);

			root.appendChild(listTag);
		}
		else if (isNumerotation(line))
		{
			if (listTag == null)
			{
				listTag = document.createElement("ol");
			}

			final Element lineTag = document.createElement("li");

			tokenize(document, lineTag, line.substring(3));

			listTag.appendChild(lineTag);

			root.appendChild(listTag);
		}
		else
		{
			listTag = null;

			final int titleLevel = computeTitleLevel(line);

			if (titleLevel > 0)
			{
				final Element lineTag = document.createElement("h" + titleLevel);

				final Text lineText = document.createTextNode(line.substring(titleLevel));

				lineTag.appendChild(lineText);

				root.appendChild(lineTag);
			}
			else
			{
				final Element lineTag = document.createElement("p");

				tokenize(document, lineTag, line);

				root.appendChild(lineTag);
			}
		}
	}

	private void tokenize(final Document document, final Element element, final String line) throws StoppedProgramException
	{
		final StarToken token = new StarToken(line);

		for (int i = 0; i < token.getGroups().size(); i++)
		{
			final Phrase subLine = token.getGroups().get(i);

			Text lineText = document.createTextNode(subLine.getText());

			if (subLine.getType() == PhraseType.NORMAL)
			{
				element.appendChild(lineText);
			}
			else
			{
				Element elementTag = null;

				if (subLine.getType() == PhraseType.ITALIC)
				{
					elementTag = document.createElement("em");

					elementTag.appendChild(lineText);
				}
				else if (subLine.getType() == PhraseType.BOLD)
				{
					elementTag = document.createElement("strong");

					elementTag.appendChild(lineText);
				}
				else if (subLine.getType() == PhraseType.BOLD_ITALIC)
				{
					Element inter = document.createElement("em");

					inter.appendChild(lineText);

					elementTag = document.createElement("strong");

					elementTag.appendChild(inter);
				}

				element.appendChild(elementTag);
			}
		}
	}

	private int computeTitleLevel(final String line)
	{
		int titleLevel = 0;

		for (final char letter : line.toCharArray())
		{
			if (letter == '#')
			{
				titleLevel++;
			}
			else
			{
				if (titleLevel == 0)
				{
					break;
				}
			}
		}

		return titleLevel;
	}

	private boolean isPuce(final String line)
	{
		boolean previousStar = false;

		for (final char letter : line.toCharArray())
		{
			if (letter == '*')
			{
				previousStar = true;
			}
			else if (letter == ' ')
			{
				return previousStar;
			}
			else
			{
				return false;
			}
		}

		return false;
	}

	private boolean isNumerotation(final String line)
	{
		boolean previousDigit = false;
		boolean previousDot = false;

		for (final char letter : line.toCharArray())
		{
			if (Character.isDigit(letter))
			{
				previousDigit = true;
			}
			else if (letter == '.' && previousDigit)
			{
				previousDot = true;
			}
			else if (letter == ' ')
			{
				return previousDot;
			}
			else
			{
				return false;
			}
		}

		return false;
	}

	private Transformer createTransformer() throws TransformerConfigurationException
	{
		// TODO cette méthode est une copie de celle de CLI_xml (à factoriser)

		final TransformerFactory transformerFactory = TransformerFactory.newInstance();

		final Transformer transformer = transformerFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		return transformer;
	}

	private class StarToken
	{
		private final List<Phrase> groups = new ArrayList<Phrase>();

		private StarToken(final String line) throws StoppedProgramException
		{
			boolean transclusion = false;

			boolean isBold = false;
			boolean isItalic = false;

			char currentCode = '\0';

			StringBuilder builder = new StringBuilder();
			StringBuilder builderTransclusion = new StringBuilder();

			for (int position = 0; position < line.length(); position++)
			{
				final char letter = line.charAt(position);

				if (currentCode == '\0')
				{
					if (letter == '*' || letter == '_')
					{
						currentCode = letter;
					}
				}

				if (letter == '{' && isTransclusion(line, position, letter))
				{
					transclusion = true;

					position++;
				}
				else if (letter == '}' && isTransclusion(line, position, letter))
				{
					final String command = api.markdownElements.get(builderTransclusion.toString());

					if (command == null)
					{
						CLI_logger.getLogger().warning(CLI_bundle.getPropertyDescription("CLI_warning_markdownKey", builderTransclusion.toString(), markdownFilepath));
					}
					else
					{
						builder.append(command);
					}

					builderTransclusion = new StringBuilder();

					transclusion = false;

					position++;
				}
				else if (isBoldItalic(line, position, currentCode))
				{
					if (! builder.toString().isEmpty())
					{
						PhraseType type;

						if (isBold && isItalic)
						{
							type = PhraseType.BOLD_ITALIC;
						}
						else
						{
							type = PhraseType.NORMAL;
						}

						Phrase phrase = new Phrase(builder.toString(), type);

						groups.add(phrase);

						builder = new StringBuilder();
					}
				}
				else if (isBold(line, position, currentCode))
				{
					if (! builder.toString().isEmpty())
					{
						PhraseType type;

						if (isBold)
						{
							type = PhraseType.BOLD;
						}
						else
						{
							type = PhraseType.NORMAL;
						}

						Phrase phrase = new Phrase(builder.toString(), type);

						groups.add(phrase);

						builder = new StringBuilder();
					}

					isBold = ! isBold;
				}
				else if (isStar(line, position, currentCode))
				{
					if (! builder.toString().isEmpty())
					{
						PhraseType type;

						if (isItalic)
						{
							type = PhraseType.ITALIC;
						}
						else
						{
							type = PhraseType.NORMAL;
						}

						Phrase phrase = new Phrase(builder.toString(), type);

						groups.add(phrase);

						builder = new StringBuilder();
					}

					isItalic = ! isItalic;

					if (! isItalic)
					{
						currentCode = '\0';
					}
				}
				else
				{
					if (transclusion)
					{
						builderTransclusion.append(letter);
					}
					else
					{
						builder.append(letter);
					}
				}
			}

			if (! builder.toString().isEmpty())
			{
				PhraseType type;

				if (isBold)
				{
					type = PhraseType.BOLD;
				}
				if (isItalic)
				{
					type = PhraseType.ITALIC;
				}
				else if (isBold && isItalic)
				{
					type = PhraseType.BOLD_ITALIC;
				}
				else
				{
					type = PhraseType.NORMAL;
				}

				Phrase phrase = new Phrase(builder.toString(), type);

				groups.add(phrase);
			}
		}

		private boolean isTransclusion(final String line, int position, final char code)
		{
			boolean isTransclusion;

			if (line.charAt(position) == code)
			{
				if (position < line.length() - 1 && line.charAt(position + 1) == code)
				{
					isTransclusion = line.charAt(position + 1) == code;
				}
				else
				{
					isTransclusion = false;
				}
			}
			else
			{
				isTransclusion = false;
			}

			return isTransclusion;
		}

		private boolean isBoldItalic(final String line, int position, final char code)
		{
			boolean isBoldItalic;

			if (line.charAt(position) == code)
			{
				if (position < line.length() - 1 && isBold(line, position + 1, code))
				{
					isBoldItalic = true;
				}
				else
				{
					isBoldItalic = false;
				}
			}
			else
			{
				isBoldItalic = false;
			}

			return isBoldItalic;
		}

		private boolean isBold(final String line, int position, final char code)
		{
			boolean isBold;

			if (line.charAt(position) == code)
			{
				if (position < line.length() - 1 && isStar(line, position + 1, code))
				{
					isBold = true;
				}
				else
				{
					isBold = false;
				}
			}
			else
			{
				isBold = false;
			}

			return isBold;
		}

		private boolean isStar(final String line, final int position, final char code)
		{
			boolean isStar;

			if (line.charAt(position) == code)
			{
				if (position == 0)
				{
					if (position < line.length() - 1)
					{
						isStar = line.charAt(position + 1) != ' ';
					}
					else
					{
						isStar = true;
					}
				}
				else if (position == line.length() - 1)
				{
					if (position > 0)
					{
						isStar = line.charAt(position - 1) != ' ';
					}
					else
					{
						isStar = true;
					}
				}
				else
				{
					if (line.charAt(position - 1) == ' ' && line.charAt(position + 1) == ' ')
					{
						isStar = false;
					}
					else if (line.charAt(position - 1) == ' ' && line.charAt(position + 1) != ' ')
					{
						isStar = true;
					}
					else if (line.charAt(position + 1) == ' ' && line.charAt(position - 1) != ' ')
					{
						isStar = true;
					}
					else
					{
						isStar = true;
					}
				}
			}
			else
			{
				isStar = false;
			}

			return isStar;
		}

		private List<Phrase> getGroups()
		{
			return groups;
		}
	}

	private class Phrase
	{
		private final PhraseType type;

		private final String text;

		private Phrase(String text, PhraseType type)
		{
			this.text = text;
			this.type = type;
		}

		private String getText()
		{
			return text;
		}

		private PhraseType getType()
		{
			return type;
		}
	}

	private enum PhraseType
	{
		BOLD,
		BOLD_ITALIC,
		ITALIC,
		NORMAL
	}
}
