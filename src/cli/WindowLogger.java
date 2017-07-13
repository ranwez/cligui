package cli;

import java.util.logging.Logger;

import cli.exceptions.StoppedProgramException;

final class WindowLogger extends CLI_logger
{
	// l'utilisation de la classe ThreadLocal permet d'outrepasser la staticité dans les threads
	// c'est-à-dire que la variable est accessible de manière statique mais aussi spécifique à
	// chaque thread
	private static final ThreadLocal<GUIconsole> THREAD = new ThreadLocal<GUIconsole>();

	static void openNewConsole(final String title)
	{
		final GUIconsole guiConsole = new GUIconsole(title);

		THREAD.set(guiConsole);
	}

	@Override
	public Logger getOutputLogger() throws StoppedProgramException
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
				return guiConsole.getLogger();
			}
		}

		throw new StoppedProgramException(); // TODO à vérifier
	}

	@Override
	public Logger getOutputErrorLogger() throws StoppedProgramException
	{
		return getOutputLogger();
	}
}
