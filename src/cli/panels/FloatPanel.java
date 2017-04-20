package cli.panels;

import java.text.NumberFormat;

import javax.swing.BoxLayout;
import javax.swing.text.NumberFormatter;

import cli.CLI_option;
import cli.CLI_output;
import cli.FocusablePanel;
import cli.OptionBean;
import cli.OptionTextField;

@SuppressWarnings("serial")
final class FloatPanel extends FocusablePanel
{
	FloatPanel(OptionBean optionBean)
	{
		super(optionBean);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		try
		{
			NumberFormatter formatter = createFormatter(optionBean.getOption());

			OptionTextField optionTextField = new OptionTextField(optionBean, formatter);

			optionTextField.addFocusListener(this);

			add(optionTextField);
		}
		catch (Exception error)
		{
			CLI_output.getOutput().printError(error);
		}
	}

	private NumberFormatter createFormatter(CLI_option option) throws IllegalArgumentException, IllegalAccessException
	{
		NumberFormat format = NumberFormat.getInstance();

		NumberFormatter formatter = new NumberFormatter(format);

		formatter.setValueClass(Float.class);

		formatter.setAllowsInvalid(false);

		return formatter;
	}
}
