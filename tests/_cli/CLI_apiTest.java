package _cli;

import org.junit.BeforeClass;
import org.junit.Test;

import cli.CLI_api;
import cli.exceptions.ProgramDoublonException;
import cli.exceptions.ProgramNotFoundException;
import data.Bill;

public class CLI_apiTest
{
	private static CLI_api api;

	@BeforeClass
	public static void initTests() throws Exception
	{
		api = new CLI_api(CLI_apiTest.class, "files/tests.properties", "prog");

		api.addProgram("bill", Bill.class);
		api.addProgram("billCopy", Bill.class);

		api.parse("-prog bill -id 4 -name table -price 42.3 -currency £ -credit");
	}

	@Test(expected = ProgramDoublonException.class)
	public void addProgram_doublon() throws Exception
	{
		api.addProgram("bill", Bill.class);
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
	public void parseOneProgramOnly() throws Exception
	{
		CLI_api api = new CLI_api("CLI.jar", "files/tests.properties", "prog");

		api.addProgram("bill", Bill.class);

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
