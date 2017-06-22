package cli;

import java.util.ArrayList;
import java.util.List;

import cli.exceptions.ProgramDoublonException;
import cli.exceptions.StoppedProgramException;
import cli.exceptions.parsing.ProgramNotFoundException;

public final class CLI_api
{
	private static final String DEBUG_OPTION_NAME = "-debug";

	private final List<CLI_program> programs = new ArrayList<CLI_program>();

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

		CLI_logger.setOutput(new ConsolesManager());

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

	public boolean checkDebug(final String commands)
	{
		return checkDebug(commands.split(" "));
	}

	public boolean checkDebug(final String[] commands)
	{
		for (final String command : commands)
		{
			if (command.equals(DEBUG_OPTION_NAME))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * This method will split a {@code String} variable in a {@code String}
	 * array and parse its values, then the corresponding program will be
	 * executed with the commands parameters.
	 * 
	 * @param commands a {@code String} variable of commands to be parsed
	 * @throws Exception if the commands are wrong of if the executed program
	 * throws an error
	 */
	public void parse(final String commands) throws Exception
	{
		parse(commands.split(" "));
	}

	/**
	 * This method will read a {@code String} array and parse its values, then
	 * the corresponding program will be executed with the commands parameters.
	 * 
	 * @param commands a {@code String} array of commands to be parsed
	 * @throws Exception if the commands are wrong of if the executed program
	 * throws an error
	 */
	public void parse(final String[] commands) throws Exception
	{
		final String programName = findProgram(commands);

		setProgramName(programName);

		String[] updatedCommands = removeSpecialOptions(commands, programName);

		currentProgram.parse(updatedCommands);
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
			if (! isProgOption(command) && ! command.equals(DEBUG_OPTION_NAME) && ! command.equals(programName))
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

		CLI_logger.getCurrentLogger().info(builder.toString());
	}
}
