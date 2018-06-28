package cli;

import java.util.logging.Logger;

import cli.exceptions.StoppedProgramException;

final class ConsoleLogger extends CLI_logger
{
	
	// call initLogger only once otherwise all messages are print twice on windows when using system.out for LOGGER and ERROR_LOGGER
	// see also CLI_consoleHAndler
	private static final Logger LOGGER = initLogger(true);
	//private static final Logger ERROR_LOGGER = initLogger(false);
	private static final Logger ERROR_LOGGER =LOGGER;
	
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
