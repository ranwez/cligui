package data;

import cli.AbstractProgram;
import cli.CLI_logger;
import cli.annotations.Delegate;
import cli.annotations.InputFile;
import cli.annotations.InternalFile;
import cli.annotations.OutputFile;
import cli.annotations.Parameter;

public final class BillProgram extends AbstractProgram
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


	@InternalFile("files/data/receivers.txt")

	@Parameter(name = "receiver")

	private String receiver = "George Washington";


	@SuppressWarnings("unused")

	private int localVariable;


	@Override
	public void execute() throws Exception
	{
		CLI_logger.getLogger().info("test OK");
	}

	@Override
	public String getXMLfilter(final String optionName)
	{
		String filter;

		if (optionName.equals("receipt") && receiver.equals("George Washington"))
		{
			filter = "receipt";
		}
		else
		{
			filter = "";
		}

		return filter;
	}
}
