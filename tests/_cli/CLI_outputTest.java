package _cli;

import java.lang.reflect.Constructor;

import org.junit.Test;

import cli.CLI_api;
import cli.CLI_output;
import cli.exceptions.StoppedProgramException;

public class CLI_outputTest
{
	@Test
	public void initBundle() throws Exception
	{
		Constructor<?> constructor = CLI_output.class.getDeclaredConstructor();

		constructor.setAccessible(true);

		constructor.newInstance();
	}

	@Test
	public void displayInfo() throws StoppedProgramException
	{
		new CLI_api("", "", ""); // initialize CLI_output

		CLI_output.getOutput().displayInfo("TEST", "test content\n");
	}
}
