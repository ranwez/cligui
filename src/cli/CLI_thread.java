package cli;

import static cli.CLI_bundleMessage.PROGRAM_FINISHED;

import java.util.Date;
import java.util.logging.Level;

import cli.exceptions.StoppedProgramException;

final class CLI_thread extends Thread
{
	private final CLI_api api;

	private final String commands;

	CLI_thread(final CLI_api api, final String commands)
	{
		this.api = api;
		this.commands = commands;

		start();
	}

	@Override
	public void run()
	{
		final Date date = new Date();

		final String consoleTitle = api.getCurrentProgram().getName() + " - " + date;

		WindowLogger.openNewConsole(consoleTitle);

		try
		{
			api.parse(commands);

			CLI_logger.getLogger().info(PROGRAM_FINISHED);
		}
		catch (Exception error)
		{
			try
			{
				CLI_logger.getErrorLogger().info(error.getMessage());

				if (api.checkDebug(commands))
				{
					CLI_logger.logError(Level.INFO, error);
				}
			}
			catch (StoppedProgramException stop)
			{
				interrupt();
			}
		}
	}
}
