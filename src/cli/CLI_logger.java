package cli;

import java.util.logging.Logger;

import cli.exceptions.StoppedProgramException;

public abstract class CLI_logger
{
	private static final String BIG_SPACING_BAR = computeLine('=', 100);
	private static final String SMALL_SPACING_BAR = computeLine('-', 100);

	private static CLI_logger output;

	static void setOutput(final CLI_logger output)
	{
		CLI_logger.output = output;
	}

	public static CLI_logger getOutput()
	{
		return output;
	}

	public static Logger getCurrentLogger() throws StoppedProgramException
	{
		return output.getLogger();
	}

	private static String computeLine(final char letter, final int nbLetters)
	{
		final StringBuilder builder = new StringBuilder();

		for (int i = 0; i < nbLetters; i++)
		{
			builder.append(letter);
		}

		return builder.toString();
	}

	public final void displayInfo(final String title, final String content) throws StoppedProgramException
	{
		final StringBuilder builder = new StringBuilder();

		builder.append(BIG_SPACING_BAR);
		builder.append("\n# ");
		builder.append(title);
		builder.append('\n');
		builder.append(SMALL_SPACING_BAR);
		builder.append('\n');
		builder.append(content);
		builder.append(BIG_SPACING_BAR);
		builder.append("\n\n");

		println(builder.toString());
	}

	public final void println(final String text) throws StoppedProgramException
	{
		getLogger().info(text + '\n');
	}

	public final void printUnusedOptionWarning(final String optionName, final boolean isUnused) throws StoppedProgramException
	{
		if (isUnused)
		{
			printBundleDescription("ConsolesManager_unusedOption", optionName);
		}
	}

	public final void printBundleDescription(final String key, final Object... values) throws StoppedProgramException
	{
		println(CLI_bundle.getPropertyDescription(key, values));
	}

	/**
	 * This method will print a {@code String} variable in an output.
	 * 
	 * @param text text to be displayed
	 * @throws StoppedProgramException if the current running program is stopped before ending
	 */
	public abstract Logger getLogger() throws StoppedProgramException;

	protected CLI_logger() {}
}
