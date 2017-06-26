package cli;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import cli.exceptions.StoppedProgramException;

final class ConsoleLogger extends CLI_logger
{
	private static final Logger LOGGER = initLogger();

	private static Logger initLogger()
	{
		final ConsoleHandler consoleHandler = new ConsoleHandler();

		consoleHandler.setFormatter(new CLI_loggerFormatter());

		final Logger logger = Logger.getLogger("console");

		logger.addHandler(consoleHandler);

		logger.setLevel(Level.OFF);

		logger.setUseParentHandlers(false);

		return logger;
	}

	@Override
	public Logger getOutputLogger() throws StoppedProgramException
	{
		return LOGGER;
	}
}
