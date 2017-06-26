package cli;

import cli.exceptions.StoppedProgramException;

public abstract class AbstractProgram
{
	protected AbstractProgram() {}

	protected abstract void execute() throws Exception;

	protected final void printUnusedOptionWarning(final String optionName, final boolean isUnused) throws StoppedProgramException
	{
		if (isUnused)
		{
			CLI_logger.getLogger().info(CLI_bundle.getPropertyDescription("CLI_option_unused", optionName));
		}
	}

	protected String getXMLfilter(final String optionName)
	{
		return "";
	}
}
