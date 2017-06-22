package _cli;

import org.junit.Test;

import cli.CLI_api;
import cli.CLI_logger;
import cli.exceptions.StoppedProgramException;

public class CLI_outputTest
{
	@Test
	public void displayInfo() throws StoppedProgramException
	{
		new CLI_api("", "", ""); // initialize CLI_output

		CLI_logger.displayInfo("TEST", "test content\n");
	}
}
