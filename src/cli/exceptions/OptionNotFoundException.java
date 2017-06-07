package cli.exceptions;

/**
 * This exception occurs when an option is not present in the program being used in the commands
 * line.
 */
@SuppressWarnings("serial")
public final class OptionNotFoundException extends CLI_exception
{
	public OptionNotFoundException(final String optionName)
	{
		super("CLI_error_optionNotFound", optionName);
	}
}
