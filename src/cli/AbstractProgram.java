package cli;

public abstract class AbstractProgram
{
	protected abstract void execute() throws Exception;

	protected String getXMLfilter(final String optionName)
	{
		return "";
	}
}
