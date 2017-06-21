package cli;

import cli.exceptions.StoppedProgramException;
import cli.logger.CLI_logger;
import cli.logger.CLI_windowHandler;

final class WindowsManager extends OutputManager
{
	// l'utilisation de la classe ThreadLocal permet d'outrepasser la staticité dans les threads
	// c'est-à-dire que la variable est accessible de manière statique mais aussi spécifique à
	// chaque thread
	private static final ThreadLocal<GUIconsole> THREAD = new ThreadLocal<GUIconsole>();

	void openNewConsole(final String title)
	{
		final GUIconsole guiConsole = new GUIconsole(title);

		CLI_logger.getLogger().addHandler(new CLI_windowHandler(guiConsole));

		THREAD.set(guiConsole);
	}

	@Override
	public void print(final String text) throws StoppedProgramException
	{
		final GUIconsole guiConsole = THREAD.get();

		if (guiConsole != null)
		{
			if (guiConsole.isDisposed())
			{
				throw new StoppedProgramException();
			}
			else
			{
				guiConsole.print(text);
			}
		}
	}
}
