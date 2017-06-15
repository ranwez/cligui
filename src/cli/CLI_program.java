package cli;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cli.annotations.Delegate;
import cli.annotations.Parameter;
import cli.exceptions.MissingOptionException;
import cli.exceptions.MissingParameterException;
import cli.exceptions.MissingRequiredOptionException;
import cli.exceptions.OptionNotFoundException;
import cli.exceptions.StoppedProgramException;

public final class CLI_program
{
	private transient final AbstractProgram program;

	private transient CLI_option option;

	private transient List<CLI_option> requiredOptions;

	private final List<CLI_option> options = new ArrayList<CLI_option>();

	private final String name;

	CLI_program(final String programName, final Class<? extends AbstractProgram> cls) throws IllegalAccessException, InstantiationException
	{
		this.name = programName;

		program = cls.newInstance();

		loadProgramOptions(cls, program);

		Collections.sort(options);
	}

	private void loadProgramOptions(final Class<?> cls, final Object instance) throws IllegalAccessException, IllegalArgumentException
	{
		final Field[] fields = cls.getDeclaredFields();

		for (final Field field : fields)
		{
			if (field.getAnnotation(Parameter.class) != null)
			{
				final CLI_option option = new CLI_option(field, instance);

				options.add(option);
			}
			else if (field.getAnnotation(Delegate.class) != null)
			{
				field.setAccessible(true);

				final Object delegationInstance = field.get(instance);

				field.setAccessible(false);

				loadProgramOptions(field.getType(), delegationInstance);
			}
		}
	}

	void parse(final String[] commands) throws Exception
	{
		resetOptionsValues();

		requiredOptions = listRequiredOptions();

		boolean prevOptionBoolean = false;

		for (final String command : commands)
		{
			if (isOption(command))
			{
				checkMissingParameters(prevOptionBoolean);

				option = findOption(command.substring(1));

				prevOptionBoolean = option.getType().equals(boolean.class);
			}
			else
			{
				if (option == null)
				{
					throw new MissingOptionException(command);
				}

				setParameter(command);

				prevOptionBoolean = false;
			}
		}

		checkMissingParameters(prevOptionBoolean);

		if (! requiredOptions.isEmpty())
		{
			throw new MissingRequiredOptionException(requiredOptions.get(0).getName());
		}

		program.execute();
	}

	void resetOptionsValues() throws IllegalAccessException, IllegalArgumentException
	{
		for (final CLI_option option : options)
		{
			option.reset();
		}
	}

	private List<CLI_option> listRequiredOptions()
	{
		final List<CLI_option> requiredOptions = new ArrayList<CLI_option>();

		for (final CLI_option option : options)
		{
			if (option.isRequired())
			{
				requiredOptions.add(option);
			}
		}

		return requiredOptions;
	}

	private boolean isOption(final String command)
	{
		boolean isOption;

		if (command.charAt(0) != '-' || command.length() == 1)
		{
			isOption = false;
		}
		else
		{
			final char secondLetter = command.charAt(1);

			isOption = ! Character.isDigit(secondLetter) && secondLetter != '-';
		}

		return isOption;
	}

	private void checkMissingParameters(final boolean prevOptionBoolean)

			throws IllegalAccessException, IllegalArgumentException, MissingParameterException
	{
		if (prevOptionBoolean)
		{
			setParameter("" + true);
		}

		if (option != null)
		{
			throw new MissingParameterException(option.getName());
		}
	}

	private void setParameter(final String parameterValue) throws IllegalAccessException, IllegalArgumentException
	{
		if (parameterValue.equals("\"\"") || parameterValue.equals("\"None\""))
		{
			option.setParameter("");
		}
		else
		{
			final String shortParameterValue = parameterValue.replace("\"", "");

			option.setParameter(shortParameterValue);
		}

		requiredOptions.remove(option);

		option = null;
	}

	private CLI_option findOption(final String optionName) throws OptionNotFoundException
	{
		for (final CLI_option option : options)
		{
			if (optionName.equals(option.getName()))
			{
				return option;
			}
		}

		throw new OptionNotFoundException(optionName);
	}

	public String getDescription()
	{
		return CLI_bundle.getPropertyDescription(name);
	}

	public String getName()
	{
		return name;
	}

	public List<CLI_option> getOptions()
	{
		return options;
	}

	String getXMLfilter(final String optionName)
	{
		return program.getXMLfilter(optionName);
	}

	/**
	 * @param optionName the name of the option to be found
	 * @return {@code true} if the option exists, else {@code false}
	 */
	public boolean hasOption(final String optionName)
	{
		boolean isOption;

		try
		{
			findOption(optionName);

			isOption = true;
		}
		catch (OptionNotFoundException error)
		{
			isOption = false;
		}

		return isOption;
	}

	/**
	 * This method will print the program options.
	 * 
	 * @throws IllegalAccessException if the option field is inaccessible
	 * @throws IllegalArgumentException if the option instance is not an
	 * instance of the option field
	 * @throws StoppedProgramException if the current running program is
	 * stopped before ending
	 */
	public void displayOptions() throws IllegalAccessException, IllegalArgumentException, StoppedProgramException
	{
		final StringBuilder builder = new StringBuilder();

		builder.append(CLI_bundle.getCitation());
		builder.append("\n\n\n");

		builder.append(name);
		builder.append(" : ");
		builder.append(getDescription());
		builder.append('\n');

		CLI_output.getOutput().println(builder.toString());

		for (final CLI_option option : options)
		{
			if (! option.isHidden())
			{
				option.display();
			}
		}
	}
}
