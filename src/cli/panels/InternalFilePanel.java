package cli.panels;

import java.awt.event.ActionEvent;

import javax.swing.JComboBox;

import cli.CLI_option;
import cli.OptionBean;

@SuppressWarnings("serial")
final class InternalFilePanel extends ComboBoxPanel
{
	InternalFilePanel(OptionBean optionBean, Object[] items)
	{
		super(optionBean, items);

		int index = getDefaultIndex(optionBean.getOption());

		getComboBox().setSelectedIndex(index);
	}

	private int getDefaultIndex(CLI_option option)
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
	public void actionPerformed(ActionEvent event)
	{
		Object selectedItem = getComboBox().getSelectedItem();

		updateOption(selectedItem);
	}
}
