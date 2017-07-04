package cli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tautua.markdownpapers.Markdown;
import org.tautua.markdownpapers.parser.ParseException;

import cli.exceptions.ProgramDoublonException;
import cli.exceptions.StoppedProgramException;
import cli.exceptions.parsing.ProgramNotFoundException;

public final class CLI_api
{
	private static final File MARKDOWN_TMP_FILE = new File("tmp.md");

	private static final String DEBUG_OPTION_NAME = "-debug";
	private static final String TIME_OPTION_NAME = "-timeTest";

	private final List<CLI_program> programs = new ArrayList<CLI_program>(); // TODO use a set ?

	private final Map<String, String> markdownElements = new LinkedHashMap<String, String>();

	private final String commandPrefix;
	private final String programOptionName;
	private final String projectName;

	private transient CLI_program currentProgram;

	/**
	 * This constructor can be used to create a {@code CLI_api} object
	 * containing several programs.
	 * 
	 * @param projectName the name of the project which will be used in the GUI
	 * commands line and in galaxy XML files creation
	 * @param bundlePath the bundle filename containing all programs, options
	 * and others descriptions
	 * @param programOptionName the name of the program selector option which
	 * is written before each program name in the commands line
	 */
	public CLI_api(final String projectName, final String bundlePath, final String programOptionName)
	{
		this.projectName = projectName;
		this.programOptionName = programOptionName;

		commandPrefix = "java -jar " + projectName + ' ';

		if (! bundlePath.isEmpty())
		{
			CLI_bundle.addProperties(bundlePath);
		}
	}

	/**
	 * This method will add a new program to the {@code CLI_api}.
	 * 
	 * @param programName the name of the program to be written in the commands
	 * line to run it
	 * @param programClass the program {@code Class} containing
	 * {@code Parameter} annotations and possibly {@code Delegate} annotations
	 * @throws IllegalAccessException if the class is not accessible
	 * @throws InstantiationException if the class cannot be instanced
	 * @throws ProgramDoublonException if the new program to be added already
	 * exists
	 */
	public void addProgram(final String programName, final Class<? extends AbstractProgram> programClass)

			throws IllegalAccessException, InstantiationException, ProgramDoublonException
	{
		for (final CLI_program program : programs)
		{
			if (programName.equals(program.getName()))
			{
				throw new ProgramDoublonException(programName);
			}
		}

		final CLI_program program = new CLI_program(programName, programClass);

		if (currentProgram == null)
		{
			currentProgram = program;
		}

		programs.add(program);
	}

	/**
	 * This method will create galaxy XML files for each program of the
	 * {@code CLI_api}.
	 * 
	 * @param projectVersion version of the project to be written in the galaxy
	 * XML files
	 * @param outputDirectory the directory which will store the galaxy XML
	 * files
	 * @throws Exception any exception caused by reflection or input / output
	 * exception
	 */
	public void exportXML(final String projectVersion, final String outputDirectory) throws Exception
	{
		final CLI_xml cli_xml = new CLI_xml(projectName, projectVersion, programOptionName);

		cli_xml.exportFiles(programs, outputDirectory);
	}

	/**
	 * This method is only used in Window mode
	 * @param commands
	 * @return
	 */
	public boolean checkDebug(final String commands)
	{
		return checkDebug(commands.split(" "));
	}

	public boolean checkDebug(final String[] commands)
	{
		return checkOptionExists(commands, DEBUG_OPTION_NAME);
	}

	private boolean checkOptionExists(final String[] commands, final String optionName)
	{
		for (final String command : commands)
		{
			if (command.equals(optionName))
			{
				return true;
			}
		}

		return false;
	}

	public void parseDocumentation(final String directoryPath, final String annotatedCommand) throws Exception
	{
		final String shortCommand = commandPrefix + annotatedCommand.replace("@", "");

		markdownElements.put(directoryPath, shortCommand);

		final String command = annotatedCommand.replace("@", directoryPath);

		parse(command);
	}

	public void exportMarkdownToHTML(final String markdownFilepath, final String htmlDirectory) throws FileNotFoundException, IOException, ParseException
	{
		createCommandsMarkdown(markdownFilepath);

		final Markdown markdown = new Markdown();

		final BufferedReader bufferedReader = new BufferedReader(new FileReader(MARKDOWN_TMP_FILE));

		final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(htmlDirectory + currentProgram.getName() + ".html"));

		markdown.transform(bufferedReader, bufferedWriter);

		bufferedWriter.close();
		bufferedReader.close();

		MARKDOWN_TMP_FILE.delete();
	}

	private void createCommandsMarkdown(final String markdownFilepath) throws FileNotFoundException, IOException
	{
		final StringBuilder builder = readMarkdown(markdownFilepath);

		final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(MARKDOWN_TMP_FILE));

		bufferedWriter.write(builder.toString());

		bufferedWriter.close();
	}

	private StringBuilder readMarkdown(final String markdownFilepath) throws FileNotFoundException, IOException
	{
		final BufferedReader bufferedReader = new BufferedReader(new FileReader(markdownFilepath));

		final StringBuilder builder = new StringBuilder();

		String line = bufferedReader.readLine();

		while (line != null)
		{
			// TODO utiliser un builder au lieu d'une regex si c'est plus rapide

			final Pattern pattern = Pattern.compile("\\{\\{.+\\}\\}");

			final Matcher matcher = pattern.matcher(line);

			if (matcher.find())
			{
				final String fullElement = matcher.group();

				final String element = fullElement.substring(2, fullElement.length() - 2) + '/';

				final String command = markdownElements.get(element);

				line = line.replace(fullElement, command);
			}

			builder.append(line);
			builder.append('\n');

			line = bufferedReader.readLine();
		}

		bufferedReader.close();

		return builder;
	}

	/**
	 * This method will split a {@code String} variable in a {@code String}
	 * array and parse its values, then the corresponding program will be
	 * executed with the commands parameters.
	 * 
	 * @param command a {@code String} variable of command to be parsed
	 * @throws Exception if the commands are wrong of if the executed program
	 * throws an error
	 */
	public void parse(final String command) throws Exception
	{
		parse(command.split(" "));
	}

	/**
	 * This method will read a {@code String} array and parse its values, then
	 * the corresponding program will be executed with the commands parameters.
	 * 
	 * @param command a {@code String} array of command to be parsed
	 * @throws Exception if the commands are wrong of if the executed program
	 * throws an error
	 */
	public void parse(final String[] command) throws Exception
	{
		final String programName = findProgram(command);

		setProgramName(programName);

		final boolean showTime = checkOptionExists(command, TIME_OPTION_NAME);

		final String[] updatedCommands = removeSpecialOptions(command, programName);

		long elapsedTime = System.currentTimeMillis();

		currentProgram.parse(updatedCommands);

		if (showTime)
		{
			final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

			final String time = dateFormat.format(new Date());

			elapsedTime = System.currentTimeMillis() - elapsedTime;

			elapsedTime /= 1000.0;

			final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("time.txt", true));

			bufferedWriter.write(time + '\t' + elapsedTime + " sec\n");

			bufferedWriter.close();
		}
	}

	private String findProgram(final String[] commands) throws ProgramNotFoundException
	{
		String programName = "";

		for (int commandID = 0; commandID < commands.length; commandID++)
		{
			final String command = commands[commandID];

			if (isProgOption(command))
			{
				if (commandID + 1 < commands.length)
				{
					programName = commands[commandID + 1];
				}

				break;
			}
		}

		if (programName.isEmpty())
		{
			throw new ProgramNotFoundException("");
		}

		return programName;
	}

	private String[] removeSpecialOptions(final String[] commands, final String programName)
	{
		final List<String> updatedCommands = new ArrayList<String>();

		for (final String command : commands)
		{
			if (! isProgOption(command) && ! command.equals(DEBUG_OPTION_NAME) && ! command.equals(TIME_OPTION_NAME) && ! command.equals(programName))
			{
				updatedCommands.add(command);
			}
		}

		return updatedCommands.toArray(new String[updatedCommands.size()]);
	}

	/**
	 * @return the current running program
	 */
	public CLI_program getCurrentProgram()
	{
		return currentProgram;
	}

	/**
	 * @return the name of the project which will be used in the GUI commands
	 * line and in galaxy XML files creation
	 */
	public String getProjectName()
	{
		return projectName;
	}

	/**
	 * @return the name of the program selector option which is written before
	 * each program name in the commands line
	 */
	public String getProgramOptionName()
	{
		return programOptionName;
	}

	public List<CLI_program> getPrograms()
	{
		return programs;
	}

	private boolean isProgOption(final String command)
	{
		return command.equals('-' + programOptionName);
	}

	void setProgramName(final String programName) throws ProgramNotFoundException
	{
		for (final CLI_program program : programs)
		{
			if (programName.equals(program.getName()))
			{
				currentProgram = program;

				return;
			}
		}

		throw new ProgramNotFoundException(programName);
	}

	/**
	 * This method will display all {@code CLI_api} programs.
	 * 
	 * @throws StoppedProgramException if the current running program is
	 * stopped before ending
	 */
	public void displayPrograms() throws StoppedProgramException
	{
		final StringBuilder builder = new StringBuilder();

		builder.append('\n');
		builder.append(CLI_bundle.getCitation());
		builder.append("\n\n\n");

		builder.append(CLI_bundle.getPropertyDescription("CLI_program_usage"));
		builder.append("\n\n");

		builder.append(CLI_bundle.getPropertyDescription("CLI_program_title"));
		builder.append(" :\n");

		for (final CLI_program program : programs)
		{
			final String description = CLI_bundle.getPropertyDescription("CLI_program_desc", program.getName(), program.getDescription());

			builder.append("  ");
			builder.append(description);
			builder.append('\n');
		}

		CLI_logger.getLogger().info(builder.toString());
	}
}
