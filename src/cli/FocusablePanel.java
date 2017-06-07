package cli;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class FocusablePanel extends JPanel implements FocusListener
{
	private final OptionBean optionBean;

	protected FocusablePanel(final OptionBean optionBean)
	{
		this.optionBean = optionBean;
	}

	protected final void updateOption(final Object value)
	{
		final CommandsPanel commandsPanel = optionBean.getCommandsPanel();

		commandsPanel.updateOptionAndCommandsLine(optionBean.getOption(), "" + value);
	}

	@Override
	public void focusGained(final FocusEvent event)
	{
		final CLI_option option = optionBean.getOption();

		final InfoTextArea optionTextArea = optionBean.getOptionTextArea();

		optionTextArea.setText(option.getDescription());
	}

	@Override
	public void focusLost(final FocusEvent event) {}
}
