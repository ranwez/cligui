package data;

import cli.Program;
import cli.annotations.Delegate;
import cli.annotations.InputFile;
import cli.annotations.InternalFile;
import cli.annotations.OutputFile;
import cli.annotations.Parameter;

public final class Bill extends Program
{
	@Delegate

	private final Product product = new Product();


	@InputFile("txt")

	@Parameter(name = "attachment")

	private String attachment = "";


	@Parameter(name = "credit", hidden = true)

	private boolean isCredit;


	@Parameter(name = "id", required = true)

	private int id;


	@Parameter(name = "priority", enumeration = BillPriority.class)

	private int levelPriority = 1;


	@OutputFile("txt")

	@Parameter(name = "receipt")

	private String receipt = "";


	@InternalFile("files/receivers.txt")

	@Parameter(name = "receiver")

	private String receiver = "George Washington";


	@SuppressWarnings("unused")

	private int localVariable;


	@Override
	public void execute() throws Exception {}


	@Override
	public String getXMLfilter(String optionName)
	{
		if (optionName.equals("receipt") && receiver.equals("George Washington"))
		{
			return "receipt";
		}

		return "";
	}
}
