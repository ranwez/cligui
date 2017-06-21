package cli.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public final class CLI_logger
{
	private static final Logger LOGGER = initLogger();

	private static Logger initLogger()
	{
		final Handler conHdlr = new ConsoleHandler();

		conHdlr.setFormatter(new CLI_loggerFormatter());

		final Logger logger = Logger.getLogger("cligui");

		logger.setUseParentHandlers(false);

		logger.addHandler(conHdlr);

		return logger;
	}

	public static Logger getLogger()
	{
		return LOGGER;
	}

	private CLI_logger() {}
}
