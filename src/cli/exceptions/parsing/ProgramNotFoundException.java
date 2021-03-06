package cli.exceptions.parsing;

/**
 * This exception occurs when a program is not present in the {@code CLI_api} being used in the
 * commands line.
 */
@SuppressWarnings("serial")
public final class ProgramNotFoundException extends CLI_parsingException
{
	public ProgramNotFoundException(final String programName)
	{
		super("CLI_error_programNotFound", programName);
	}
}
