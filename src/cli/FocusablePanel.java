package cli;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class FocusablePanel extends JPanel implements FocusListener
{
	private final OptionBean optionBean;

	protected FocusablePanel(OptionBean optionBean)
	{
		this.optionBean = optionBean;
	}

	protected final void updateOption(Object value)
	{
		CommandsPanel commandsPanel = optionBean.getCommandsPanel();

		commandsPanel.updateOptionAndCommandsLine(optionBean.getOption(), "" + value);
	}

	@Override
	public void focusGained(FocusEvent event)
	{
		CLI_option option = optionBean.getOption();

		InfoTextArea optionTextArea = optionBean.getOptionTextArea();

		optionTextArea.setText(option.getDescription());
	}

	@Override
	public void focusLost(FocusEvent event) {}
}
