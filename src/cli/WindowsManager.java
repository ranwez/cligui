package cli;

import cli.exceptions.StoppedProgramException;

final class WindowsManager extends OutputManager
{
	// l'utilisation de la classe ThreadLocal permet d'outrepasser la staticité dans les threads
	// c'est-à-dire que la variable est accessible de manière statique mais aussi spécifique à
	// chaque thread
	private static final ThreadLocal<GUIconsole> THREAD = new ThreadLocal<GUIconsole>();

	void openNewConsole(String title)
	{
		THREAD.set(new GUIconsole(title));
	}

	@Override
	public void print(String text) throws StoppedProgramException
	{
		GUIconsole guiConsole = THREAD.get();

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
