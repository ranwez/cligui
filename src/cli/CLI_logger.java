package cli;

import java.util.logging.Logger;

import cli.exceptions.StoppedProgramException;

public abstract class CLI_logger
{
	private static CLI_logger cliLogger;

	public static Logger getLogger() throws StoppedProgramException
	{
		return cliLogger.getOutputLogger();
	}

	static void setOutput(final CLI_logger cliLogger)
	{
		CLI_logger.cliLogger = cliLogger;
	}

	public static void printUnusedOptionWarning(final String optionName, final boolean isUnused) throws StoppedProgramException
	{
		if (isUnused)
		{
			printBundleDescription("ConsolesManager_unusedOption", optionName);
		}
	}

	public static void printBundleDescription(final String key, final Object... values) throws StoppedProgramException
	{
		getLogger().info(CLI_bundle.getPropertyDescription(key, values));
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
