package data;

import cli.AbstractProgram;
import cli.annotations.InternalFile;
import cli.annotations.Parameter;

public final class WrongProgram extends AbstractProgram
{
	@InternalFile("files/wrongName.txt")

	@Parameter(name = "")

	private String wrongVariable = "";

	@Override
	public void execute() throws Exception {}

	@Override
	public String getXMLfilter(final String optionName)
	{
		return "";
	}
}
