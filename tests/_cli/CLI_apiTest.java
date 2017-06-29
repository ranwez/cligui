package _cli;

import org.junit.BeforeClass;
import org.junit.Test;

import cli.CLI_api;
import cli.exceptions.ProgramDoublonException;
import cli.exceptions.parsing.ProgramNotFoundException;
import data.BillProgram;
import gui.WindowRunner;

public class CLI_apiTest
{
	private static CLI_api api;

	@BeforeClass
	public static void initTests() throws Exception
	{
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
	public void displayPrograms() throws Exception
	{
		api.displayPrograms();
	}

	@Test
	public void unusedGetters() throws ProgramNotFoundException
	{
		api.getProjectName();
		api.getProgramOptionName();
		api.getPrograms();
	}
}
