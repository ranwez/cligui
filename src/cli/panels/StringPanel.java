package cli.panels;

import javax.swing.BoxLayout;

import cli.FocusablePanel;
import cli.OptionBean;
import cli.OptionTextField;

@SuppressWarnings("serial")
final class StringPanel extends FocusablePanel
{
	StringPanel(OptionBean optionBean)
	{
		super(optionBean);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		OptionTextField textField = new OptionTextField(optionBean);

		textField.addFocusListener(this);

		add(textField);
	}
}
