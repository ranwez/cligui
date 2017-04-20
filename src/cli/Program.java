package cli;

public abstract class Program
{
	protected abstract void execute() throws Exception;

	protected String getXMLfilter(String optionName)
	{
		return "";
	}
}
