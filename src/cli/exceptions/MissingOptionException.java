package cli.exceptions;

/**
 * This exception occurs when an option is missing before a value in the commands line.
 */
@SuppressWarnings("serial")
public final class MissingOptionException extends CLI_exception
{
	public MissingOptionException(String parameter)
	{
		super("CLI_error_missingOption", parameter);
	}
}
