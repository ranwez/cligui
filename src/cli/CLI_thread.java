package cli;

import java.util.Date;
import java.util.logging.Level;

import cli.exceptions.StoppedProgramException;

public final class CLI_thread extends Thread
{
	private final CLI_api api;

	private final String commands;

	public CLI_thread(final CLI_api api, final String commands)
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

			CLI_logger.getLogger().info(CLI_bundle.getPropertyDescription("CLI_program_finished"));
		}
		catch (Exception error)
		{
			try
			{
				CLI_logger.getLogger().info(error.getMessage());

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
