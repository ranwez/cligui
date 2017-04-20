package cli.exceptions;

/**
 * This exception occurs in the window mode when a running program is stopped by the user.
 */
@SuppressWarnings("serial")
public final class StoppedProgramException extends Exception
{
	public StoppedProgramException() {}
}
