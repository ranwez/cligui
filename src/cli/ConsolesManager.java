package cli;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import cli.exceptions.StoppedProgramException;

final class ConsolesManager extends CLI_logger
{
	private static final Logger LOGGER = initLogger();

	private static Logger initLogger()
	{
		final ConsoleHandler consoleHandler = new ConsoleHandler();

		consoleHandler.setFormatter(new CLI_loggerFormatter());

		Logger logger = Logger.getLogger("console");

		logger.addHandler(consoleHandler);

		logger.setUseParentHandlers(false);

		return logger;
	}

	@Override
	public Logger getLogger() throws StoppedProgramException
	{
		return LOGGER;
	}
}
