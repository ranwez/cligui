package cli.panels;

import javax.swing.JLabel;

import cli.CLI_bundle;
import cli.FocusablePanel;
import cli.OptionBean;

@SuppressWarnings("serial")
final class UndefinedPanel extends FocusablePanel
{
	UndefinedPanel(OptionBean optionBean)
	{
		super(optionBean);

		JLabel label = new JLabel(CLI_bundle.getPropertyDescription("CLI_window_undefinedPanel"));

		add(label);
	}
}
