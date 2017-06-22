package cli;

import java.util.logging.Logger;

import cli.exceptions.StoppedProgramException;

public abstract class CLI_logger
{
	private static final String BIG_SPACING_BAR = computeLine('=', 100);
	private static final String SMALL_SPACING_BAR = computeLine('-', 100);

	private static CLI_logger cliLogger;

	private static String computeLine(final char letter, final int nbLetters)
	{
		final StringBuilder builder = new StringBuilder();

		for (int i = 0; i < nbLetters; i++)
		{
			builder.append(letter);
		}

		return builder.toString();
	}

	public static void displayInfo(final String title, final String content) throws StoppedProgramException
	{
		final StringBuilder builder = new StringBuilder();

		builder.append('\n');
		builder.append(BIG_SPACING_BAR);
		builder.append("\n# ");
		builder.append(title);
		builder.append('\n');
		builder.append(SMALL_SPACING_BAR);
		builder.append('\n');
		builder.append(content);
		builder.append(BIG_SPACING_BAR);
		builder.append("\n\n");

		getLogger().info(builder.toString());
	}

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
