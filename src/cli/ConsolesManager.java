package cli;

import cli.exceptions.StoppedProgramException;

final class ConsolesManager extends OutputManager
{
	@Override
	public void print(final String text) throws StoppedProgramException
	{
		System.out.print(text);
	}
}
