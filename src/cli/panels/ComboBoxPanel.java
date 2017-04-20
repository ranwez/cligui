package cli.panels;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

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

		for (Object item : items)
		{
			comboBox.addItem(item);
		}

		add(comboBox);

		comboBox.addActionListener(this);
		comboBox.addFocusListener(this);
	}

	protected final JComboBox<Object> getComboBox()
	{
		return comboBox;
	}
}
