package gui;

import cli.CLI_api;
import cli.CLI_window;
import data.Bill;

public final class WindowTest
{
	public static String PROJECT_NAME = "cligui.jar";

	public static void main(String[] args) throws Exception
	{
		CLI_api api = new CLI_api(PROJECT_NAME, "files/tests.properties", "prog");

		api.addProgram("bill", Bill.class);

		new CLI_window(api);
	}
}
