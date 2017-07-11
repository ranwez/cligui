package cli.exceptions;

/**
 * This exception occurs when a program with a similar name has already been added to a
 * {@code CLI_api}.
 */
@SuppressWarnings("serial")
public final class CLI_fileException extends CLI_exception
{
	public CLI_fileException(final String filepath)
	{
		super("CLI_error_file", filepath);
	}
}
