package markdown;

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

public final class CLI_markdown
{
	public CLI_markdown(final String markdownFilepath, final String htmlFilepath) throws Exception
	{
		final DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

		final DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

		final Document document = documentBuilder.newDocument();

		final BufferedReader bufferedReader = new BufferedReader(new FileReader(markdownFilepath));

		final Element root = document.createElement("html");

		boolean isList = false;

		Element lineTag;
		Element listTag = null;

		StringBuilder lineBuilder = new StringBuilder();

		String line = bufferedReader.readLine();

		// note : le test "null" permet de s'assurer que la dernière ligne soit bien ajoutée
		// sinon, il faut ajouter une ligne vide supplémentaire

		while (line != null || ! lineBuilder.toString().isEmpty())
		{
			if (line == null || line.isEmpty())
			{
				line = lineBuilder.toString();

				boolean isPuce = isPuce(line);
				boolean isNumerotation = isNumerotation(line);

				if (isPuce)
				{
					line = line.substring(2);
				}
				else if (isNumerotation)
				{
					line = line.substring(3);
				}

				int titleLevel = computeTitleLevel(line);

				Text lineText;

				if (isPuce)
				{
					lineText = document.createTextNode(line);

					lineTag = document.createElement("li");

					tokenize(document, lineTag, line);

					if (! isList)
					{
						listTag = document.createElement("ul");

						isList = true;
					}

					listTag.appendChild(lineTag);

					root.appendChild(listTag);
				}
				else if (isNumerotation)
				{
					lineText = document.createTextNode(line);

					lineTag = document.createElement("li");

					tokenize(document, lineTag, line);

					if (! isList)
					{
						listTag = document.createElement("ol");

						isList = true;
					}

					listTag.appendChild(lineTag);

					root.appendChild(listTag);
				}
				else
				{
					if (isList)
					{
						root.appendChild(listTag);

						isList = false;
					}

					if (titleLevel > 0)
					{
						lineTag = document.createElement("h" + titleLevel);

						lineText = document.createTextNode(line.substring(titleLevel));

						lineTag.appendChild(lineText);

						root.appendChild(lineTag);
					}
					else
					{
						lineTag = document.createElement("p");

						tokenize(document, lineTag, line);

						lineText = document.createTextNode(line);

						root.appendChild(lineTag);
					}
				}

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

			line = bufferedReader.readLine();
		}

		bufferedReader.close();

		document.appendChild(root);

		final DOMSource source = new DOMSource(document);

		createTransformer().transform(source, new StreamResult(htmlFilepath));
	}

	private void tokenize(final Document document, final Element element, final String line)
	{
		final StarToken token = new StarToken(line);

		for (int i = 0; i < token.getGroups().size(); i++)
		{
			final Phrase subLine = token.getGroups().get(i);

			Text lineText = document.createTextNode(subLine.getText());

			//if (i % 2 == 0) // TODO utiliser un opérateur binaire pour améliorer les performances
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

	public class StarToken
	{
		private final List<Phrase> groups = new ArrayList<Phrase>();

		public StarToken(final String line)
		{
			boolean star = false;

			StringBuilder builder = new StringBuilder();

			for (int position = 0; position < line.length(); position++)
			{
				final char letter = line.charAt(position);

				if (isBoldItalic(line, position, '*') || isBoldItalic(line, position, '_'))
				{
					if (! builder.toString().isEmpty())
					{
						PhraseType type;

						if (star)
						{
							type = PhraseType.BOLD_ITALIC;
						}
						else
						{
							type = PhraseType.NORMAL;
						}

						star = ! star;

						Phrase phrase = new Phrase(builder.toString(), type);

						groups.add(phrase);

						builder = new StringBuilder();
					}
				}
				else if (isBold(line, position, '*') || isBold(line, position, '_'))
				{
					if (! builder.toString().isEmpty())
					{
						PhraseType type;

						if (star)
						{
							type = PhraseType.BOLD;
						}
						else
						{
							type = PhraseType.NORMAL;
						}

						star = ! star;

						Phrase phrase = new Phrase(builder.toString(), type);

						groups.add(phrase);

						builder = new StringBuilder();
					}
				}
				else if (isStar(line, position, '*') || isStar(line, position, '_'))
				{
					if (! builder.toString().isEmpty())
					{
						PhraseType type;

						if (star)
						{
							type = PhraseType.ITALIC;
						}
						else
						{
							type = PhraseType.NORMAL;
						}

						star = ! star;

						Phrase phrase = new Phrase(builder.toString(), type);

						groups.add(phrase);

						builder = new StringBuilder();
					}
				}
				else
				{
					builder.append(letter);
				}
			}

			if (! builder.toString().isEmpty())
			{
				PhraseType type;

				if (star)
				{
					type = PhraseType.ITALIC;
				}
				else
				{
					type = PhraseType.NORMAL;
				}

				Phrase phrase = new Phrase(builder.toString(), type);

				groups.add(phrase);
			}
		}

		private boolean isBoldItalic(final String line, int position, final char code)
		{
			boolean isBoldItalic;

			if (line.charAt(position) == code)
			{
				if (position < line.length() - 1 && isBold(line, position + 1, code))
				{
					isBoldItalic = isBold(line, position + 1, code);
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
					isBold = isStar(line, position + 1, code);
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
				if (position > 0 && line.charAt(position - 1) != ' ')
				{
					isStar = line.charAt(position - 1) != ' ';
				}
				else if (position < line.length() - 1 && line.charAt(position + 1) != ' ')
				{
					isStar = line.charAt(position + 1) != ' ';
				}
				else
				{
					isStar = false;
				}
			}
			else
			{
				isStar = false;
			}

			return isStar;
		}

		public List<Phrase> getGroups()
		{
			return groups;
		}
	}

	public class Phrase
	{
		private final PhraseType type;

		private final String text;

		public Phrase(String text, PhraseType type)
		{
			this.text = text;
			this.type = type;
		}

		public String getText()
		{
			return text;
		}

		public PhraseType getType()
		{
			return type;
		}
	}

	public enum PhraseType
	{
		BOLD,
		BOLD_ITALIC,
		ITALIC,
		NORMAL
	}
}
