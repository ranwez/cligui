package cli.panels;

import javax.swing.JComboBox;

import cli.CLI_option;
import cli.OptionBean;
import cli.exceptions.StoppedProgramException;

@SuppressWarnings("serial")
final class InternalFilePanel extends ComboBoxPanel
{
	InternalFilePanel(OptionBean optionBean, String[] items) throws StoppedProgramException
	{
		super(optionBean, items);
	}

	@Override
	protected int getDefaultIndex(CLI_option option)
	{
		JComboBox<Object> comboBox = getComboBox();

		String filename = option.getDefaultValue().toString();

		for (int itemID = 0; itemID < comboBox.getItemCount(); itemID++)
		{
			if (comboBox.getItemAt(itemID).equals(filename))
			{
				return itemID;
			}
		}

		return 0;
	}

	@Override
	protected Object getSelectedValue()
	{
		return getComboBox().getSelectedItem().toString();
	}
}
