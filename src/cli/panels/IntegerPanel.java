package cli.panels;

import java.text.NumberFormat;

import javax.swing.BoxLayout;
import javax.swing.text.NumberFormatter;

import cli.FocusablePanel;
import cli.OptionBean;
import cli.OptionTextField;

@SuppressWarnings("serial")
final class IntegerPanel extends FocusablePanel
{
	IntegerPanel(OptionBean optionBean)
	{
		super(optionBean);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		NumberFormatter formatter = createFormatter();

		OptionTextField optionTextField = new OptionTextField(optionBean, formatter);

		add(optionTextField);

		optionTextField.addFocusListener(this);
	}

	private NumberFormatter createFormatter()
	{
		NumberFormat format = NumberFormat.getInstance();

		NumberFormatter formatter = new NumberFormatter(format);

		formatter.setValueClass(Integer.class);

		formatter.setAllowsInvalid(false);

		return formatter;
	}
}
