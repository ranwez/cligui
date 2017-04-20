package cli.exceptions;

/**
 * This exception occurs when a program is not present in the {@code CLI_api} being used in the
 * commands line.
 */
@SuppressWarnings("serial")
public final class ProgramNotFoundException extends CLI_exception
{
	public ProgramNotFoundException(String programName)
	{
		super("CLI_error_programNotFound", programName);
	}
}
