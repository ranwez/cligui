package runnables;

import java.io.File;

import cli.CLI_logger;
import cli.exceptions.StoppedProgramException;

public final class RemoveComputedFiles
{
	private static final String[] FILEPATHES =
		{
				"markdown/bill.html",
				"tests/bill.xml"
		};

	public static void main(final String[] args) throws StoppedProgramException
	{
		for (final String filepath : FILEPATHES)
		{
			final File file = new File(filepath);

			file.delete();
		}

		CLI_logger.getLogger().info("Files successfully deleted");
	}
}
