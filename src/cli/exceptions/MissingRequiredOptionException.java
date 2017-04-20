package cli.exceptions;

/**
 * This exception occurs when a required option is not present in the commands line.
 */
@SuppressWarnings("serial")
public final class MissingRequiredOptionException extends CLI_exception
{
	public MissingRequiredOptionException(String optionName)
	{
		super("CLI_error_missingRequiredOption", optionName);
	}
}
