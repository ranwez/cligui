package cli;

import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

final class CLI_windowHandler extends StreamHandler
{
	private final GUIconsole guiConsole;

	CLI_windowHandler(final GUIconsole guiConsole)
	{
		super(System.out, new CLI_loggerFormatter());

		this.guiConsole = guiConsole;
	}

	@Override
	public void publish(final LogRecord record)
	{
		if (isLoggable(record))
		{
			final String message = getFormatter().format(record);

			guiConsole.getTextArea().append(message);
		}
	}

	@Override
	public void flush() {}

	@Override
	public void close() throws SecurityException {}
}
