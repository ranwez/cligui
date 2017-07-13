package cli;

import java.awt.GraphicsEnvironment;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cli.exceptions.ProgramDoublonException;
import cli.exceptions.StoppedProgramException;
import cli.exceptions.parsing.ProgramNotFoundException;

import static cli.CLI_bundleMessage.*;
import static cli.CLI_bundleKey.*;

public final class CLI_api
{
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

	/**
	 * This method will parse a command and add its content in an internal
	 * array.
	 * @param markdownKey the key name to be used in the markdown file to get
	 * the command
	 * @param directoryPath the path which will be used to replace '@' letters
	 * @param annotatedCommand the command to be parsed with relative pathes
	 * (starting with '@' letter)
	 * @throws Exception
	 */
	public void parseDocumentation(final String markdownKey, final String directoryPath, final String annotatedCommand) throws Exception
	{
		final String shortCommand = commandPrefix + annotatedCommand.replace("@", "");

		markdownElements.put(markdownKey, shortCommand);

		final String command = annotatedCommand.replace("@", directoryPath);

		parse(command);
	}

	/**
	 * This method will read a markdown file and export it into a HTML file
	 * with the commands defined in the {@link parseDocumentation} method.
	 * @param markdownFilepath the markdown filepath
	 * @param htmlFilepath the HTML filepath to be used for markdown exporting
	 * @throws Exception
	 */
	public void exportMarkdownToHTML(final String markdownFilepath, final String htmlFilepath) throws Exception
	{
		new CLI_markdown(this, markdownFilepath, htmlFilepath);
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

	Map<String, String> getMarkdownElements()
	{
		return markdownElements;
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
	 * This method will display the fonts available on your operating system,
	 * you can then use the "CLI_font_name" in your bundle file to change the
	 * window font (the size can also be changed with "CLI_font_size").
	 * @throws StoppedProgramException
	 */
	public void displayAvailableFonts() throws StoppedProgramException
	{
		final String[] fontsNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

		for (final String fontName : fontsNames)
		{
			CLI_logger.getLogger().info(fontName);
		}
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
		builder.append(WINDOW_MENU_CITATION_MESSAGE);
		builder.append("\n\n\n");

		builder.append(PROGRAM_USAGE);
		builder.append("\n\n");

		builder.append(PROGRAM_TITLE);
		builder.append(" :\n");

		for (final CLI_program program : programs)
		{
			final String description = CLI_bundle.getPropertyDescription(PROGRAM_DESCRIPTION, program.getName(), program.getDescription());

			builder.append("  ");
			builder.append(description);
			builder.append('\n');
		}

		CLI_logger.getLogger().info(builder.toString());
	}
}
