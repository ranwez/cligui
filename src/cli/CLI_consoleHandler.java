package cli;

import java.io.PrintStream;
import java.util.logging.ConsoleHandler;

final class CLI_consoleHandler extends ConsoleHandler
{
	CLI_consoleHandler(final boolean isStdOut)
	{
		PrintStream stream;

		if (isStdOut)
		{
			stream = System.out;
		}
		else
		{
			stream = System.err;
		}

		setOutputStream(stream);
	}
}
