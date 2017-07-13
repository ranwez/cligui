package cli;

import java.util.logging.Logger;

import cli.exceptions.StoppedProgramException;

final class ConsoleLogger extends CLI_logger
{
	private static final Logger ERROR_LOGGER = initLogger(false);
	private static final Logger LOGGER = initLogger(true);

	private static Logger initLogger(final boolean isStdOut)
	{
		final CLI_consoleHandler consoleHandler = new CLI_consoleHandler(isStdOut);

		consoleHandler.setFormatter(new CLI_loggerFormatter());

		final Logger logger = Logger.getLogger("console");

		logger.addHandler(consoleHandler);

		logger.setUseParentHandlers(false);

		return logger;
	}

	@Override
	public Logger getOutputLogger() throws StoppedProgramException
	{
		return LOGGER;
	}

	@Override
	public Logger getOutputErrorLogger() throws StoppedProgramException
	{
		return ERROR_LOGGER;
	}
}
