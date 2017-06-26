package cli;

import java.util.logging.Level;
import java.util.logging.Logger;

import cli.exceptions.StoppedProgramException;

public abstract class CLI_logger
{
	private static CLI_logger cliLogger = new ConsoleLogger();

	public static Logger getLogger() throws StoppedProgramException
	{
		return cliLogger.getOutputLogger();
	}

	static void setWindowOutput()
	{
		cliLogger = new WindowLogger();
	}

	public static void logError(final Level level, final Exception error) throws StoppedProgramException
	{
		CLI_logger.getLogger().log(level, error.toString());

		final StackTraceElement[] traces = error.getStackTrace();

		for (final StackTraceElement trace : traces)
		{
			CLI_logger.getLogger().log(level, "\tat " + trace.toString());
		}
	}

	/**
	 * This method will print a {@code String} variable in an output.
	 * 
	 * @param text text to be displayed
	 * @throws StoppedProgramException if the current running program is stopped before ending
	 */
	public abstract Logger getOutputLogger() throws StoppedProgramException;

	protected CLI_logger() {}
}
