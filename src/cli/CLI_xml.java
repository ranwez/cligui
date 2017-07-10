package cli;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import cli.annotations.InputFile;
import cli.annotations.EnumFromInternalFile;
import cli.annotations.OutputFile;
import cli.annotations.Parameter;

final class CLI_xml
{
	private transient final String programOptionName;
	private transient final String projectName;
	private transient final String projectVersion;

	CLI_xml(final String projectName, final String projectVersion, final String programOptionName) throws Exception
	{
		this.projectName = projectName;
		this.projectVersion = projectVersion;
		this.programOptionName = programOptionName;
	}

	void exportFiles(final List<CLI_program> programs, final String outputDirectory) throws Exception
	{
		for (final CLI_program program : programs)
		{
			exportFile(program, outputDirectory);
		}
	}

	private void exportFile(final CLI_program program, final String outputDirectory) throws Exception
	{
		final String url = outputDirectory + program.getName() + ".xml";

		final DOMSource source = new DOMSource(createDocument(program));

		createTransformer().transform(source, new StreamResult(url));

		CLI_logger.getLogger().info("File \"" + url + "\" was created successfully.");
	}

	private Transformer createTransformer() throws TransformerConfigurationException
	{
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();

		final Transformer transformer = transformerFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		return transformer;
	}

	private Document createDocument(final CLI_program program) throws Exception
	{
		final DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

		final DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

		final Document document = documentBuilder.newDocument();

		document.appendChild(createTool(document, program));

		return document;
	}

	private Element createTool(final Document document, final CLI_program program) throws Exception
	{
		final Element tool = document.createElement("tool");
		tool.setAttribute("id", program.getName());
		tool.setAttribute("name", program.getName());
		tool.setAttribute("version", projectVersion);

		tool.appendChild(createText(document, program.getDescription(), "description"));
		tool.appendChild(createCommand(document, program));
		tool.appendChild(createInputs(document, program));
		tool.appendChild(createOutputs(document, program));
		tool.appendChild(createText(document, program.getDescription(), "help"));

		return tool;
	}

	private Element createText(final Document document, final String nodeContent, final String nodeName)
	{
		final Text text = document.createTextNode(nodeContent);

		final Element description = document.createElement(nodeName);

		description.appendChild(text);

		return description;
	}

	private Element createCommand(final Document document, final CLI_program program)
	{
		final Text commands = document.createTextNode(createStringCommand(program));

		final Element command = document.createElement("command");

		command.appendChild(commands);

		return command;
	}

	private String createStringCommand(final CLI_program program)
	{
		final StringBuilder builder = new StringBuilder(projectName);

		if (! programOptionName.isEmpty())
		{
			builder.append(" -");
			builder.append(programOptionName);
			builder.append(' ');
			builder.append(program.getName());
		}

		for (final CLI_option option : program.getOptions())
		{
			if (! option.isHidden())
			{
				builder.append(createCommandOption(option));
			}
		}

		return builder.toString();
	}

	private String createCommandOption(final CLI_option option)
	{
		final boolean isString = option.getType().equals(String.class);

		final StringBuilder builder = new StringBuilder();

		builder.append(" -");
		builder.append(option.getName());
		builder.append(' ');

		if (isString)
		{
			builder.append("\\\"");
		}

		builder.append('$');
		builder.append(option.getName());

		if (isString)
		{
			builder.append("\\\"");
		}

		return builder.toString();
	}

	private Element createInputs(final Document document, final CLI_program program) throws Exception
	{
		final Element input = document.createElement("inputs");

		createInputsIfRequired(document, program, input, true);
		createInputsIfRequired(document, program, input, false);

		return input;
	}

	private void createInputsIfRequired(final Document document, final CLI_program program, final Element input,

			final boolean isRequired) throws Exception
	{
		for (final CLI_option option : program.getOptions())
		{
			if (! option.isHidden() && option.getAnnotation(OutputFile.class) == null && option.isRequired() == isRequired)
			{
				input.appendChild(createParam(document, option));
			}
		}
	}

	private Element createParam(final Document document, final CLI_option option) throws Exception
	{
		final Element param = document.createElement("param");

		String className;

		if (option.getAnnotation(InputFile.class) != null)
		{
			className = "data";
		}
		else if (option.getAnnotation(EnumFromInternalFile.class) != null)
		{
			createStringComboBox(document, param, option);

			className = "select";
		}
		else
		{
			final Class<?> optionClass = option.getType();

			if (optionClass.equals(int.class))
			{
				final Parameter parameter = option.getAnnotation(Parameter.class);

				if (parameter != null && ! parameter.enumeration().equals(Object.class))
				{
					createIntegerComboBox(document, param, option);

					className = "select";
				}
				else
				{
					className = "integer";
				}
			}
			else if (optionClass.equals(String.class) || optionClass.equals(char.class))
			{
				className = "text";
			}
			else if (optionClass.equals(double.class))
			{
				className = "float";
			}
			else
			{
				className = optionClass.toString();
			}
		}

		param.setAttribute("label", option.getName() + " : " + option.getDescription());
		param.setAttribute("name", option.getName());
		param.setAttribute("type", className);

		if (! className.equals("select"))
		{
			final String defaultValue = option.getDefaultValue().toString();

			if (! defaultValue.isEmpty())
			{
				param.setAttribute("value", defaultValue);
			}

			if (! option.isRequired())
			{
				param.setAttribute("optional", "true");
			}
		}

		return param;
	}

	private void createStringComboBox(final Document document, final Element param, final CLI_option option) throws IOException
	{
		final boolean isInteger = option.getType().equals(int.class);

		final EnumFromInternalFile annotation = option.getAnnotation(EnumFromInternalFile.class);

		final String filesPath = annotation.value();

		final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filesPath);

		if (inputStream == null)
		{
			throw new FileNotFoundException("file not found : " + filesPath);
		}

		final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

		final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String line = bufferedReader.readLine();

		while (line != null)
		{
			final Element optionTag = document.createElement("option");

			String value;

			if (isInteger)
			{
				final String name = line.substring(3);

				final Text text = document.createTextNode(name);

				optionTag.appendChild(text);

				value = line.substring(0, 2);
			}
			else
			{
				value = line;
			}

			optionTag.setAttribute("value", value);

			if (line.equals(option.getDefaultValue()))
			{
				optionTag.setAttribute("selected", "true");
			}

			param.appendChild(optionTag);

			line = bufferedReader.readLine();
		}

		inputStream.close();
	}

	private void createIntegerComboBox(final Document document, final Element param, final CLI_option option) throws Exception
	{
		final Parameter parameter = option.getAnnotation(Parameter.class);

		final Object[] list = (Object[]) parameter.enumeration().getMethod("values").invoke(null);

		for (int elementID = 0; elementID < list.length; elementID++)
		{
			final Element optionTag = document.createElement("option");

			optionTag.setAttribute("value", "" + elementID);

			if (elementID == (Integer) option.getDefaultValue())
			{
				optionTag.setAttribute("selected", "true");
			}

			optionTag.appendChild(document.createTextNode("" + list[elementID]));

			param.appendChild(optionTag);
		}
	}

	private Element createOutputs(final Document document, final CLI_program program)
	{
		Element output = document.createElement("outputs");

		for (CLI_option option : program.getOptions())
		{
			final OutputFile outputFile = option.getAnnotation(OutputFile.class);

			if (! option.isHidden() && outputFile != null)
			{
				final String format = outputFile.value().toLowerCase();

				final Element param = document.createElement("data");

				param.setAttribute("format", format);
				param.setAttribute("label", option.getName());
				param.setAttribute("name", option.getName());

				final String xmlFilter = program.getXMLfilter(option.getName());

				if (! xmlFilter.isEmpty())
				{
					final Element filter = createText(document, xmlFilter, "filter");

					param.appendChild(filter);
				}

				output.appendChild(param);
			}
		}

		return output;
	}
}
