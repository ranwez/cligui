package cli.panels;

import java.awt.event.ActionEvent;

import cli.OptionBean;

@SuppressWarnings("serial")
class BooleanPanel extends ComboBoxPanel
{
	private static final Boolean[] BOOLEAN_VALUES = {true, false};

	BooleanPanel(final OptionBean optionBean)
	{
		super(optionBean, BOOLEAN_VALUES);

		getComboBox().setSelectedItem(false);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		Object selectedItem = getComboBox().getSelectedItem();

		updateOption(selectedItem);
	}
}
