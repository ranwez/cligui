package markdown;

import java.io.BufferedReader;
import java.io.FileReader;

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

		StringBuilder line = new StringBuilder();

		String lineString = bufferedReader.readLine();

		while (lineString != null)
		{
			if (lineString.isEmpty())
			{
				boolean isPuce = isPuce(line.toString());

				int tabsCount = computeTabsCount(line.toString());

				int titleLevel = computeTitleLevel(line.toString());

				//System.out.println(isPuce(line.toString()) + " => " + line.toString());

				Text lineText;

				if (isPuce)
				{
					lineText = document.createTextNode(line.toString().substring(2));

					lineTag = document.createElement("li");

					lineTag.appendChild(lineText);

					if (! isList)
					{
						listTag = document.createElement("ul");

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

						lineText = document.createTextNode(line.toString());

						lineTag.appendChild(lineText);

						root.appendChild(lineTag);
					}
				}

				line = new StringBuilder();
			}
			else
			{
				line.append(lineString);
			}

			lineString = bufferedReader.readLine();
		}

		bufferedReader.close();

		document.appendChild(root);

		final DOMSource source = new DOMSource(document);

		createTransformer().transform(source, new StreamResult(htmlFilepath));
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

	private int computeTabsCount(final String line)
	{
		int nbSpaces = 0;
		int nbTabs = 0;

		for (final char letter : line.toCharArray())
		{
			if (letter == '\t')
			{
				nbTabs++;
			}
			else if (letter == ' ')
			{
				nbSpaces++;
			}
			else
			{
				break;
			}
		}

		return nbTabs + nbSpaces / 4;
	}

	private boolean isTabbed(final String line)
	{
		int nbSpaces = 0;

		for (final char letter : line.toCharArray())
		{
			if (letter == '\t')
			{
				return true;
			}
			else if (letter == ' ')
			{
				nbSpaces++;

				if (nbSpaces == 4)
				{
					return true;
				}
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
}
