package cli;

public final class OptionBean
{
	private final CLI_option option;

	private final CommandsPanel commandsPanel;

	private final InfoTextArea optionTextArea;

	OptionBean(CommandsPanel commandsPanel, CLI_option option, InfoTextArea optionTextArea)
	{
		this.commandsPanel = commandsPanel;
		this.option = option;
		this.optionTextArea = optionTextArea;
	}

	public CLI_option getOption()
	{
		return option;
	}

	CommandsPanel getCommandsPanel()
	{
		return commandsPanel;
	}

	InfoTextArea getOptionTextArea()
	{
		return optionTextArea;
	}
}
