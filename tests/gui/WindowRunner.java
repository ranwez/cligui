package gui;

import java.util.logging.Level;

import cli.CLI_api;
import cli.CLI_logger;
import cli.CLI_window;
import data.BillProgram;

public final class WindowRunner
{
	public static final String PROJECT_NAME = "cligui.jar";

	public static void main(final String[] args) throws Exception
	{
		CLI_logger.getLogger().setLevel(Level.INFO);

		final CLI_api api = new CLI_api(PROJECT_NAME, "files/bundle/tests.properties", "prog");

		api.addProgram("bill", BillProgram.class);
		/*
		api.parseDocumentation("mark/option01/", "-prog bill -id 1 -name p1 -price 12.5");

		api.parseDocumentation("mark/option02/", "-prog bill -id 7 -name p2 -price 7.25");

		api.exportMarkdown("mark/markdown.md");
		 */
		new CLI_window(api);
	}

	private WindowRunner() {}
}
