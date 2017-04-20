package cli.panels;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import cli.CLI_option;
import cli.FocusablePanel;
import cli.OptionBean;

/**
 * This class can be specialized to create customized ComboBox panels.
 */
@SuppressWarnings("serial")
public abstract class ComboBoxPanel extends FocusablePanel implements ActionListener
{
	private final JComboBox<Object> comboBox = new JComboBox<Object>();

	/**
	 * @param optionBean an object pointing to a {@code CLI_option} object
	 * @param items an {@code Object} array
	 */
	protected ComboBoxPanel(OptionBean optionBean, Object[] items)
	{
		super(optionBean);

		setLayout(new FlowLayout(FlowLayout.LEFT));

		fillComboBox(optionBean.getOption(), items);

		int defaultIndex = getDefaultIndex(optionBean.getOption());

		comboBox.setSelectedIndex(defaultIndex);

		add(comboBox);

		comboBox.addActionListener(this);
		comboBox.addFocusListener(this);
	}

	private void fillComboBox(CLI_option option, Object[] items)
	{
		Object item;

		for (int itemID = 0; itemID < items.length; itemID++)
		{
			item = items[itemID];

			comboBox.addItem(item);
		}
	}

	protected final JComboBox<Object> getComboBox()
	{
		return comboBox;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Object selectedValue = getSelectedValue();

		updateOption(selectedValue);
	}

	/**
	 * This method is used to define the default selected item with its index.
	 * 
	 * @param option the {@code CLI_option} linked to the ComboBox
	 * @return the default selected item index
	 */
	protected abstract int getDefaultIndex(CLI_option option);

	/**
	 * This method is used to define how the selected item should be read to get its value.
	 * 
	 * @return the value of the selected item
	 */
	protected abstract Object getSelectedValue();
}
