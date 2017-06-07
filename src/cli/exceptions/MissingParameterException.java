package cli.exceptions;

/**
 * This exception occurs when an option is not followed by a value (except for boolean options).
 */
@SuppressWarnings("serial")
public final class MissingParameterException extends CLI_exception
{
	public MissingParameterException(final String optionName)
	{
		super("CLI_error_missingParameter", optionName);
	}
}
