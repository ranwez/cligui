package _cli;

import java.util.logging.Level;

import org.junit.BeforeClass;
import org.junit.Test;

import cli.CLI_api;
import cli.CLI_logger;
import cli.exceptions.ProgramDoublonException;
import cli.exceptions.parsing.ProgramNotFoundException;
import data.BillProgram;
import runnables.WindowRunner;

public class CLI_apiTest
{
	private static CLI_api api;

	@BeforeClass
	public static void initTests() throws Exception
	{
		CLI_logger.getLogger().setLevel(Level.OFF);

		api = new CLI_api(WindowRunner.PROJECT_NAME, "files/bundle/tests.properties", "prog");

		api.addProgram("bill", BillProgram.class);
		api.addProgram("billCopy", BillProgram.class);

		api.parse("-prog bill -id 4 -name table -price 42.3 -currency £ -credit");
	}

	@Test(expected = ProgramDoublonException.class)
	public void addProgram_doublon() throws Exception
	{
		api.addProgram("bill", BillProgram.class);
	}

	@Test(expected = ProgramNotFoundException.class)
	public void setProgramName_wrongName() throws Exception
	{
		api.parse("-prog wrongProgram");
	}

	@Test(expected = ProgramNotFoundException.class)
	public void findProgram_noProgName() throws Exception
	{
		api.parse("-prog");
	}

	@Test(expected = ProgramNotFoundException.class)
	public void findProgram_noProgOption() throws Exception
	{
		api.parse("-id 4 -name table -price 42.3 -currency £ -credit");
	}

	@Test
	public void exportMarkdownToHTML() throws Exception
	{
		api.parseDocumentation("credit", "bill/credit/", "-prog bill -id 1 -name p1 -price 12.5");

		api.exportMarkdownToHTML("markdown/markTest.md", "result.html");
	}
}
