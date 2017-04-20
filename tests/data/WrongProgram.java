package data;

import cli.Program;
import cli.annotations.InternalFile;
import cli.annotations.Parameter;

public final class WrongProgram extends Program
{
	@InternalFile("files/wrongName.txt")

	@Parameter(name = "")

	private String wrongVariable = "";

	@Override
	public void execute() throws Exception {}

	@Override
	public String getXMLfilter(String optionName)
	{
		return "";
	}
}
