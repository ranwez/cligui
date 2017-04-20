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
import cli.annotations.InternalFile;
import cli.annotations.OutputFile;
import cli.annotations.Parameter;

final class CLI_xml
{
	private final String programOptionName;
	private final String projectName;
	private final String projectVersion;

	CLI_xml(String projectName, String projectVersion, String programOptionName) throws Exception
	{
		this.projectName = projectName;
		this.projectVersion = projectVersion;
		this.programOptionName = programOptionName;
	}

	void exportFiles(List<CLI_program> programs, String outputDirectory) throws Exception
	{
		for (CLI_program program : programs)
		{
			exportFile(program, outputDirectory);
		}
	}

	private void exportFile(CLI_program program, String outputDirectory) throws Exception
	{
		String url = outputDirectory + program.getName() + ".xml";

		DOMSource source = new DOMSource(createDocument(program));

		createTransformer().transform(source, new StreamResult(url));

		CLI_output.getOutput().println("File \"" + url + "\" was created successfully.");
	}

	private Transformer createTransformer() throws TransformerConfigurationException
	{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();

		Transformer transformer = transformerFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		return transformer;
	}

	private Document createDocument(CLI_program program) throws Exception
	{
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

		Document document = documentBuilder.newDocument();

		document.appendChild(createTool(document, program));

		return document;
	}

	private Element createTool(Document document, CLI_program program) throws Exception
	{
		Element tool = document.createElement("tool");
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

	private Element createText(Document document, String nodeContent, String nodeName)
	{
		Text text = document.createTextNode(nodeContent);

		Element description = document.createElement(nodeName);

		description.appendChild(text);

		return description;
	}

	private Element createCommand(Document document, CLI_program program)
	{
		Text commands = document.createTextNode(createStringCommand(program));

		Element command = document.createElement("command");

		command.appendChild(commands);

		return command;
	}

	private String createStringCommand(CLI_program program)
	{
		StringBuilder builder = new StringBuilder(projectName);

		if (! programOptionName.isEmpty())
		{
			builder.append(" -");
			builder.append(programOptionName);
			builder.append(' ');
			builder.append(program.getName());
		}

		for (CLI_option option : program.getOptions())
		{
			if (! option.isHidden())
			{
				builder.append(createCommandOption(option));
			}
		}

		return builder.toString();
	}

	private String createCommandOption(CLI_option option)
	{
		boolean isString = option.getType().equals(String.class);

		StringBuilder builder = new StringBuilder();

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

	private Element createInputs(Document document, CLI_program program) throws Exception
	{
		Element input = document.createElement("inputs");

		createInputsIfRequired(document, program, input, true);
		createInputsIfRequired(document, program, input, false);

		return input;
	}

	private void createInputsIfRequired(Document document, CLI_program program, Element input, boolean isRequired) throws Exception
	{
		for (CLI_option option : program.getOptions())
		{
			if (! option.isHidden() && option.getAnnotation(OutputFile.class) == null && option.isRequired() == isRequired)
			{
				input.appendChild(createParam(document, option));
			}
		}
	}

	private Element createParam(Document document, CLI_option option) throws Exception
	{
		Element param = document.createElement("param");

		String className;

		if (option.getAnnotation(InputFile.class) != null)
		{
			className = "data";
		}
		else if (option.getAnnotation(InternalFile.class) != null)
		{
			createStringComboBox(document, param, option);

			className = "select";
		}
		else
		{
			Class<?> optionClass = option.getType();

			if (optionClass.equals(int.class))
			{
				Parameter parameter = option.getAnnotation(Parameter.class);

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
			String defaultValue = option.getDefaultValue().toString();

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

	private void createStringComboBox(Document document, Element param, CLI_option option) throws IOException
	{
		boolean isInteger = option.getType().equals(int.class);

		Element optionTag;

		InternalFile annotation = option.getAnnotation(InternalFile.class);

		String filesPath = annotation.value();
		String name;
		String value;

		Text text;

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filesPath);

		if (inputStream == null)
		{
			throw new FileNotFoundException("file not found : " + filesPath);
		}

		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String line = bufferedReader.readLine();

		while (line != null)
		{
			optionTag = document.createElement("option");

			if (isInteger)
			{
				value = line.substring(0, 2);
				name = line.substring(3);

				text = document.createTextNode(name);

				optionTag.appendChild(text);
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

	private void createIntegerComboBox(Document document, Element param, CLI_option option) throws Exception
	{
		Element optionTag;

		Parameter parameter = option.getAnnotation(Parameter.class);

		Object[] list = (Object[]) parameter.enumeration().getMethod("values").invoke(null);

		for (int elementID = 0; elementID < list.length; elementID++)
		{
			optionTag = document.createElement("option");

			optionTag.setAttribute("value", "" + elementID);

			if (elementID == (Integer) option.getDefaultValue())
			{
				optionTag.setAttribute("selected", "true");
			}

			optionTag.appendChild(document.createTextNode("" + list[elementID]));

			param.appendChild(optionTag);
		}
	}

	private Element createOutputs(Document document, CLI_program program)
	{
		Element filter;
		Element output = document.createElement("outputs");
		Element param;

		OutputFile outputFile;

		String format;

		for (CLI_option option : program.getOptions())
		{
			outputFile = option.getAnnotation(OutputFile.class);

			if (! option.isHidden() && outputFile != null)
			{
				format = outputFile.value().toLowerCase();

				param = document.createElement("data");

				param.setAttribute("format", format);
				param.setAttribute("label", option.getName());
				param.setAttribute("name", option.getName());

				String xmlFilter = program.getXMLfilter(option.getName());

				if (! xmlFilter.isEmpty())
				{
					filter = createText(document, xmlFilter, "filter");

					param.appendChild(filter);
				}

				output.appendChild(param);
			}
		}

		return output;
	}
}
