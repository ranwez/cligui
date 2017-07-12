package cli;

public final class OptionBean
{
	private final CLI_option option;

	private final CommandPanel commandsPanel;

	private final InfoTextArea optionTextArea;

	OptionBean(final CommandPanel commandsPanel, final CLI_option option, final InfoTextArea optionTextArea)
	{
		this.commandsPanel = commandsPanel;
		this.option = option;
		this.optionTextArea = optionTextArea;
	}

	public CLI_option getOption()
	{
		return option;
	}

	CommandPanel getCommandsPanel()
	{
		return commandsPanel;
	}

	InfoTextArea getOptionTextArea()
	{
		return optionTextArea;
	}
}
