package data;

import cli.AbstractProgram;
import cli.annotations.EnumFromInternalFile;
import cli.annotations.Parameter;

public final class WrongProgram extends AbstractProgram
{
	@EnumFromInternalFile("files/data/wrongName.txt")

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
