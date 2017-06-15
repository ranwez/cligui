package _cli;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;

import org.junit.BeforeClass;
import org.junit.Test;

import cli.CLI_api;
import cli.CLI_option;
import cli.CLI_program;
import cli.exceptions.MissingOptionException;
import cli.exceptions.MissingParameterException;
import cli.exceptions.MissingRequiredOptionException;
import cli.exceptions.OptionNotFoundException;
import data.Bill;
import data.WrongProgram;
import gui.WindowTest;

public class CLI_programTest
{
	private static CLI_api api;

	@BeforeClass
	public static void initTests() throws Exception
	{
		api = new CLI_api(WindowTest.PROJECT_NAME, "files/tests.properties", "prog");

		api.addProgram("bill", Bill.class);
	}

	@Test
	public void CLI_xmlTest() throws Exception
	{
		api.exportXML("testVersion", "tests/");
	}

	@Test(expected = FileNotFoundException.class)
	public void CLI_xmlTest_internalFileNotFound() throws Exception
	{
		CLI_api api = new CLI_api(WindowTest.PROJECT_NAME, "files/tests.properties", "prog");

		api.addProgram("wrongProgram", WrongProgram.class);

		api.exportXML("testVersion", "tests/xml/");
	}

	@Test
	public void parse() throws Exception
	{
		api.parse("-prog bill -id 4 -name table -price 42.3 -credit -currency £");

		CLI_option option = findOption("currency");

		assertEquals("$", "" + option.getDefaultValue());
		assertEquals("£", "" + option.getValue());
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
