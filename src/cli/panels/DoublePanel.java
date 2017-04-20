package cli.panels;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.text.NumberFormatter;

import cli.CLI_option;
import cli.CLI_output;
import cli.FocusablePanel;
import cli.OptionBean;
import cli.OptionTextField;

@SuppressWarnings("serial")
class DoublePanel extends FocusablePanel
{
	DoublePanel(OptionBean optionBean)
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
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);

		DecimalFormat format = new DecimalFormat("0.0##");

		format.setDecimalFormatSymbols(symbols);

		NumberFormatter formatter = new NumberFormatter(format);

		formatter.setValueClass(Double.class);

		formatter.setAllowsInvalid(false);

		return formatter;
	}
}
