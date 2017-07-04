package runnables;

import cli.CLI_api;
import cli.CLI_window;
import data.BillProgram;

public final class WindowRunner
{
	public static final String PROJECT_NAME = "cligui.jar";

	public static void main(final String[] args) throws Exception
	{
		final CLI_api api = new CLI_api(PROJECT_NAME, "files/bundle/tests.properties", "prog");

		api.addProgram("bill", BillProgram.class);

		new CLI_window(api);
	}

	private WindowRunner() {}
}
