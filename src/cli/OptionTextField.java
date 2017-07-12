package cli;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JFormattedTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

@SuppressWarnings("serial")
public final class OptionTextField extends JFormattedTextField implements CaretListener, FocusListener
{
	private static final Color BACKGROUND_COLOR = CLI_bundle.getBundleColor("CLI_ihm_optionsAreaBackground");
	private static final Color HIDDEN_COLOR = CLI_bundle.getBundleColor("CLI_ihm_optionsAreaHidden");
	private static final Color NORMAL_COLOR = CLI_bundle.getBundleColor("CLI_ihm_optionsArea");

	private final OptionBean optionBean;

	public OptionTextField(final OptionBean optionBean)
	{
		this(optionBean, null);
	}

	public OptionTextField(final OptionBean optionBean, final AbstractFormatter formatter)
	{
		super(formatter);

		this.optionBean = optionBean;

		setBackground(BACKGROUND_COLOR);
		setOpaque(false);

		final String textValue = optionBean.getOption().getDefaultValue().toString();

		setText(textValue);

		hideText();

		addCaretListener(this);
		addFocusListener(this);
	}

	@Override
	public void caretUpdate(final CaretEvent event)
	{
		final CLI_option option = optionBean.getOption();

		final String currentValue = getText();

		if (currentValue.equals(option.getDefaultValue().toString()))
		{
			hideText();
		}
		else
		{
			if (! currentValue.isEmpty())
			{
				setForeground(NORMAL_COLOR);
			}
		}

		optionBean.getCommandsPanel().updateOptionAndCommandsLine(option, currentValue);
	}

	@Override
	public void focusGained(final FocusEvent event) {}

	@Override
	public void focusLost(final FocusEvent event)
	{
		if (getText().isEmpty())
		{
			setText(optionBean.getOption().getDefaultValue().toString());

			hideText();
		}
	}

	private void hideText()
	{
		if (! getText().isEmpty())
		{
			setForeground(HIDDEN_COLOR);
		}
	}
}
