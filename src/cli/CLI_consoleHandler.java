package cli;

import java.io.PrintStream;
import java.util.logging.ConsoleHandler;

final class CLI_consoleHandler extends ConsoleHandler
{
	// always use System.out otherwise not correctly print on linux ???!!!
	// see also consoleLogger
	CLI_consoleHandler(final boolean isStdOut)
	{
		PrintStream stream;

		if (isStdOut)
		{
			stream = System.out;
		}
		else
		{
			//stream = System.err; //some messages are not visible on linux cluster using system.err
			stream = System.out; // everything is write twice ?
		}

		setOutputStream(stream);
	}
}
