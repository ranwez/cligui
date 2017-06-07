package cli.panels;

import java.awt.event.ActionEvent;

import javax.swing.JComboBox;

import cli.CLI_option;
import cli.OptionBean;

@SuppressWarnings("serial")
final class InternalFilePanel extends ComboBoxPanel
{
	InternalFilePanel(final OptionBean optionBean, final Object[] items)
	{
		super(optionBean, items);

		final int index = getDefaultIndex(optionBean.getOption());

		getComboBox().setSelectedIndex(index);
	}

	private int getDefaultIndex(final CLI_option option)
	{
		final JComboBox<Object> comboBox = getComboBox();

		final String filename = option.getDefaultValue().toString();

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
	public void actionPerformed(final ActionEvent event)
	{
		final Object selectedItem = getComboBox().getSelectedItem();

		updateOption(selectedItem);
	}
}
