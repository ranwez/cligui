package gui;

import _cli.CLI_apiTest;
import cli.CLI_api;
import cli.CLI_window;
import data.Bill;

public final class WindowTest
{
	public static void main(String[] args) throws Exception
	{
		CLI_api api = new CLI_api(CLI_apiTest.class, "files/tests.properties", "prog");

		api.addProgram("bill", Bill.class);

		new CLI_window(api);
	}
}
