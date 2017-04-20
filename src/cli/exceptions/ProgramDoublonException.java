package cli.exceptions;

/**
 * This exception occurs when a program with a similar name has already been added to a
 * {@code CLI_api}.
 */
@SuppressWarnings("serial")
public final class ProgramDoublonException extends CLI_exception
{
	public ProgramDoublonException(String programName)
	{
		super("CLI_error_programDoublon", programName);
	}
}
