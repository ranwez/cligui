package cli;

public final class CLI_output
{
	private static OutputManager output;

	static void setOutput(final OutputManager output)
	{
		CLI_output.output = output;
	}

	public static OutputManager getOutput()
	{
		return output;
	}

	private CLI_output() {}
}
