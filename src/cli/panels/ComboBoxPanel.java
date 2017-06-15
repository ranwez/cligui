package cli.panels;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import cli.AbstractFocusablePanel;
import cli.OptionBean;

/**
 * This class can be specialized to create customized ComboBox panels.
 */
@SuppressWarnings("serial")
public abstract class ComboBoxPanel extends AbstractFocusablePanel implements ActionListener
{
	private final JComboBox<Object> comboBox = new JComboBox<Object>();

	/**
	 * @param optionBean an object pointing to a {@code CLI_option} object
	 * @param items an {@code Object} array
	 */
	protected ComboBoxPanel(final OptionBean optionBean, final Object[] items)
	{
		super(optionBean);

		setLayout(new FlowLayout(FlowLayout.LEFT));

		for (final Object item : items)
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
