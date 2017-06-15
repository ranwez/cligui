package cli.panels;

import javax.swing.BoxLayout;

import cli.AbstractFocusablePanel;
import cli.OptionBean;
import cli.OptionTextField;

@SuppressWarnings("serial")
final class StringPanel extends AbstractFocusablePanel
{
	StringPanel(final OptionBean optionBean)
	{
		super(optionBean);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		final OptionTextField textField = new OptionTextField(optionBean);

		textField.addFocusListener(this);

		add(textField);
	}
}
