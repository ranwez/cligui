package cli.logger;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

import cli.GUIconsole;

public final class CLI_windowHandler extends StreamHandler
{
	private final GUIconsole guiConsole;

	public CLI_windowHandler(final GUIconsole guiConsole)
	{
		this.guiConsole = guiConsole;

		LogManager manager = LogManager.getLogManager();

		String className = this.getClass().getName();

		String level = manager.getProperty(className + ".level");

		setLevel(level != null ? Level.parse(level) : Level.INFO);
	}

	@Override
	public void publish(final LogRecord record)
	{
		// TODO uncomment
		//if (isLoggable(record))
		{
			final CLI_loggerFormatter formatter = new CLI_loggerFormatter();

			final String message = formatter.format(record);

			guiConsole.print(message);
		}
	}

	@Override
	public void flush() {}

	@Override
	public void close() throws SecurityException {}
}
