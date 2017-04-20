package cli;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import cli.exceptions.ProgramDoublonException;
import cli.exceptions.ProgramNotFoundException;
import cli.exceptions.StoppedProgramException;

public final class CLI_api
{
	private static String findProjectName(Class<?> cls)
	{
		String jarPath = "";

		try
		{
			URL jarURL = cls.getProtectionDomain().getCodeSource().getLocation();

			jarPath = URLDecoder.decode(jarURL.getPath(), "UTF-8");
		}
		catch (UnsupportedEncodingException error)
		{
			CLI_output.getOutput().printError(error);
		}

		File jarFile = new File(jarPath);

		if (jarFile.isFile()) // .../project.jar
		{
			int index = jarPath.lastIndexOf(File.separatorChar) + 1;

			return jarPath.substring(index);
		}
		else // .../project/bin/
		{
			String[] directories = jarPath.split(File.separator);

			int index = directories.length - 2;

			return directories[index] + ".jar";
		}
	}

	private final List<CLI_program> programs = new ArrayList<CLI_program>();

	private final String programOptionName;
	private final String projectName;

	private CLI_program currentProgram;

	/**
	 * This constructor can be used to create a {@code CLI_api} object containing several programs,
	 * the project name is the same as the java project name.
	 * 
	 * @param cls a class of the current java project, usually the class using this constructor
	 * @param bundlePath the bundle filename containing all programs, options and others
	 * descriptions
	 * @param programOptionName the name of the program selector option which is written before
	 * each program name in the commands line (useless if only one program)
	 */
	public CLI_api(Class<?> cls, String bundlePath, String programOptionName)
	{
		this(findProjectName(cls), bundlePath, programOptionName);
	}

	/**
	 * This constructor can be used to create a {@code CLI_api} object containing several programs.
	 * 
	 * @param projectName the name of the project which will be used in the GUI commands line and
	 * in galaxy XML files creation
	 * @param bundlePath the bundle filename containing all programs, options and others
	 * descriptions
	 * @param programOptionName the name of the program selector option which is written before
	 * each program name in the commands line (useless if only one program)
	 */
	public CLI_api(String projectName, String bundlePath, String programOptionName)
	{
		this.projectName = projectName;
		this.programOptionName = programOptionName;

		CLI_output.setOutput(new ConsolesManager());

		if (! bundlePath.isEmpty())
		{
			CLI_bundle.addProperties(bundlePath);
		}
	}

	/**
	 * This method will add a new program to the {@code CLI_api}.
	 * 
	 * @param programName the name of the program to be written in the commands line to run it
	 * @param programClass the program {@code Class} containing {@code Parameter} annotations and
	 * possibly {@code Delegate} annotations
	 * @throws IllegalAccessException if the class is not accessible
	 * @throws InstantiationException if the class cannot be instanced
	 * @throws ProgramDoublonException if the new program to be added already exists
	 */
	public void addProgram(String programName, Class<? extends Program> programClass)

			throws IllegalAccessException, InstantiationException, ProgramDoublonException
	{
		for (CLI_program program : programs)
		{
			if (programName.equals(program.getName()))
			{
				throw new ProgramDoublonException(programName);
			}
		}

		CLI_program program = new CLI_program(programName, programClass);

		if (currentProgram == null)
		{
			currentProgram = program;
		}

		programs.add(program);
	}

	/**
	 * This method will create galaxy XML files for each program of the {@code CLI_api}.
	 * 
	 * @param projectVersion version of the project to be written in the galaxy XML files
	 * @param outputDirectory the directory which will store the galaxy XML files
	 * @throws Exception any exception caused by reflection or input / output exception
	 */
	public void exportXML(String projectVersion, String outputDirectory) throws Exception
	{
		CLI_xml cli_xml = new CLI_xml(projectName, projectVersion, programOptionName);

		cli_xml.exportFiles(programs, outputDirectory);
	}

	/**
	 * This method will split a {@code String} variable in a {@code String} array and parse its
	 * values, then the corresponding program will be executed with the commands parameters.
	 * 
	 * @param commands a {@code String} variable of commands to be parsed
	 * @throws Exception if the commands are wrong of if the executed program throws an error
	 */
	public void parse(String commands) throws Exception
	{
		parse(commands.split(" "));
	}

	/**
	 * This method will read a {@code String} array and parse its values, then the corresponding
	 * program will be executed with the commands parameters.
	 * 
	 * @param commands a {@code String} array of commands to be parsed
	 * @throws Exception if the commands are wrong of if the executed program throws an error
	 */
	public void parse(String[] commands) throws Exception
	{
		String programName;

		String[] updatedCommands;

		if (programs.size() > 1)
		{
			programName = findProgram(commands);

			setProgramName(programName);

			updatedCommands = removeProgOption(commands, programName);
		}
		else
		{
			programName = currentProgram.getName();

			updatedCommands = commands;
		}

		currentProgram.parse(updatedCommands);
	}

	private String findProgram(String[] commands) throws ProgramNotFoundException
	{
		String programName = "";

		String command;

		for (int commandID = 0; commandID < commands.length; commandID++)
		{
			command = commands[commandID];

			if (isProgramOption(command))
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

	private String[] removeProgOption(String[] commands, String programName)
	{
		List<String> updatedCommands = new ArrayList<String>();

		for (String command : commands)
		{
			if (! isProgramOption(command) && ! command.equals(programName))
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
	 * @return the name of the project which will be used in the GUI commands line and in galaxy
	 * XML files creation
	 */
	public String getProjectName()
	{
		return projectName;
	}

	/**
	 * @return the name of the program selector option which is written before each program name in
	 * the commands line (useless if only one program)
	 */
	public String getProgramOptionName()
	{
		return programOptionName;
	}

	public List<CLI_program> getPrograms()
	{
		return programs;
	}

	private boolean isProgramOption(String command)
	{
		return command.equals('-' + programOptionName);
	}

	/**
	 * Adds a citation before each program/option help and in the window help menu.
	 * 
	 * @param citation the text to be displayed in console and window
	 */
	public void setCitation(String citation)
	{
		CLI_bundle.setCitation(citation);
	}

	void setProgramName(String programName) throws ProgramNotFoundException
	{
		for (CLI_program program : programs)
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
	 * @throws StoppedProgramException if the current running program is stopped before ending
	 */
	public void displayPrograms() throws StoppedProgramException
	{
		String description;

		StringBuilder builder = new StringBuilder();

		builder.append('\n');
		builder.append(CLI_bundle.getCitation());
		builder.append("\n\n\n");

		builder.append(CLI_bundle.getPropertyDescription("CLI_program_usage"));
		builder.append("\n\n");

		builder.append(CLI_bundle.getPropertyDescription("CLI_program_title"));
		builder.append(" :\n");

		for (CLI_program program : programs)
		{
			description = CLI_bundle.getPropertyDescription("CLI_program_desc", program.getName(), program.getDescription());

			builder.append("  ");
			builder.append(description);
			builder.append('\n');
		}

		CLI_output.getOutput().println(builder.toString());
	}
}
