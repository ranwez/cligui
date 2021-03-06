package _cli;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.junit.BeforeClass;
import org.junit.Test;

import cli.CLI_api;
import cli.CLI_logger;
import cli.CLI_option;
import cli.CLI_program;
import cli.exceptions.CLI_fileException;
import cli.exceptions.parsing.MissingOptionException;
import cli.exceptions.parsing.MissingParameterException;
import cli.exceptions.parsing.MissingRequiredOptionException;
import cli.exceptions.parsing.OptionNotFoundException;
import data.BillProgram;
import data.WrongProgram;
import runnables.WindowRunner;

public class CLI_programTest
{
	private static CLI_api api;

	@BeforeClass
	public static void initTests() throws Exception
	{
		CLI_logger.getLogger().setLevel(Level.OFF);

		api = new CLI_api(WindowRunner.PROJECT_NAME, "files/bundle/tests.properties", "prog");

		api.addProgram("bill", BillProgram.class);
	}

	@Test
	public void CLI_xmlTest() throws Exception
	{
		api.exportXML("testVersion", "tests/");
	}

	@Test(expected = FileNotFoundException.class)
	public void CLI_xmlTest_internalFileNotFound() throws Exception
	{
		CLI_api wrongAPI = new CLI_api(WindowRunner.PROJECT_NAME, "files/bundle/tests.properties", "prog");

		wrongAPI.addProgram("wrongProgram", WrongProgram.class);

		wrongAPI.exportXML("testVersion", "tests/xml/");
	}

	@Test
	public void parse() throws Exception
	{
		api.parse("-prog bill -id 4 -name table -price 42.3 -credit -currency �");

		CLI_option option = findOption("currency");

		assertEquals("$", "" + option.getDefaultValue());
		assertEquals("�", "" + option.getValue());
	}

	@Test
	public void parse_emptyChar() throws Exception
	{
		api.parse("-prog bill -id 4 -name \"\" -price 42.3 -credit -currency \"\"");

		CLI_option option = findOption("currency");

		assertEquals('\0', option.getValue());
	}

	@Test
	public void parse_emptyString() throws Exception
	{
		api.parse("-prog bill -id 4 -name \"\" -price 42.3 -credit -currency £");

		CLI_option option01 = findOption("name");

		assertEquals("", option01.getValue());


		api.parse("-prog bill -id 4 -name \"None\" -price 42.3 -credit -currency £");

		CLI_option option02 = findOption("name");

		assertEquals("", option02.getValue());

		api.getCurrentProgram().displayOptions();
	}

	@Test(expected = CLI_fileException.class)
	public void parse_fileInputException_directory() throws Exception
	{
		api.parse("-prog bill -id 4 -name table -price 42.3 -credit -currency £ -attachment wrongDirectory/bill.md");
	}

	@Test(expected = CLI_fileException.class)
	public void parse_fileInputException_file() throws Exception
	{
		api.parse("-prog bill -id 4 -name table -price 42.3 -credit -currency £ -attachment markdown/wrongFile");
	}

	@Test(expected = CLI_fileException.class)
	public void parse_fileOutputException_directory() throws Exception
	{
		api.parse("-prog bill -id 4 -name table -price 42.3 -credit -currency £ -receipt wrongDirectory/bill.md");
	}

	@Test
	public void parse_fileOutputException_file() throws Exception
	{
		api.parse("-prog bill -id 4 -name table -price 42.3 -credit -currency £ -receipt markdown/wrongFile");
	}

	@Test(expected = MissingOptionException.class)
	public void parse_missingOption() throws Exception
	{
		api.parse("-prog bill -id 4 -name table -price 42.3 £");
	}

	@Test(expected = MissingParameterException.class)
	public void parse_missingParameter() throws Exception
	{
		api.parse("-prog bill -id");
	}

	@Test(expected = MissingRequiredOptionException.class)
	public void parse_missingRequiredOption() throws Exception
	{
		api.parse("-prog bill -id 4 -name table");
	}

	@Test
	public void parse_negativeDigit() throws Exception
	{
		api.parse("-prog bill -id 4 -name table -price -7 -credit -currency £");

		CLI_option option = findOption("price");

		assertEquals(-7.0f, option.getValue());
	}

	@Test
	public void parse_tirets() throws Exception
	{
		api.parse("-prog bill -id 4 -name -- -price 42.3 -credit -currency -");

		CLI_option option01 = findOption("name");
		CLI_option option02 = findOption("currency");

		assertEquals("--", "" + option01.getValue());
		assertEquals("-", "" + option02.getValue());
	}

	@Test(expected = OptionNotFoundException.class)
	public void parse_wrongOption() throws Exception
	{
		api.parse("-prog bill -wrongOption");
	}

	@Test
	public void unusedGetters()
	{
		api.getCurrentProgram().getOptions();
	}

	private CLI_option findOption(String optionName) throws Exception
	{
		CLI_program program = api.getCurrentProgram();

		Method method = program.getClass().getDeclaredMethod("findOption", String.class);

		method.setAccessible(true);

		CLI_option option = (CLI_option) method.invoke(program, optionName);

		method.setAccessible(false);

		return option;
	}
}
