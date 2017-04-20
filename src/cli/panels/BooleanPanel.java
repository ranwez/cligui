package cli.panels;

import cli.CLI_option;
import cli.OptionBean;

@SuppressWarnings("serial")
class BooleanPanel extends ComboBoxPanel
{
	BooleanPanel(OptionBean optionBean)
	{
		super(optionBean, new String[]{"YES", "NO"});
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

		if (selectedValue.equals("YES"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
