package cli.panels;

import cli.CLI_option;
import cli.OptionBean;

@SuppressWarnings("serial")
class BooleanPanel extends ComboBoxPanel
{
	private static final String[] BOOLEAN_VALUES = {"TRUE", "FALSE"};

	BooleanPanel(OptionBean optionBean)
	{
		super(optionBean, BOOLEAN_VALUES);
	}

	@Override
	protected int getDefaultIndex(CLI_option option)
	{
		return 1;
	}

	@Override
	protected Object getSelectedValue()
	{
		String selectedValue = getComboBox().getSelectedItem().toString();

		return Boolean.parseBoolean(selectedValue);
	}
}
