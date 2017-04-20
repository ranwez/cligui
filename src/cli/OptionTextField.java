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
	private final OptionBean optionBean;

	public OptionTextField(OptionBean optionBean)
	{
		this(optionBean, null);
	}

	public OptionTextField(OptionBean optionBean, AbstractFormatter formatter)
	{
		super(formatter);

		this.optionBean = optionBean;

		setOpaque(false);

		String textValue = optionBean.getOption().getDefaultValue().toString();

		setText(textValue);

		hideText();

		addCaretListener(this);
		addFocusListener(this);
	}

	@Override
	public void caretUpdate(CaretEvent event)
	{
		CLI_option option = optionBean.getOption();

		String currentValue = getText();

		if (currentValue.equals(option.getDefaultValue().toString()))
		{
			hideText();
		}
		else
		{
			if (! currentValue.isEmpty())
			{
				setForeground(Color.BLACK);
			}
		}

		optionBean.getCommandsPanel().updateOptionAndCommandsLine(option, currentValue);
	}

	@Override
	public void focusGained(FocusEvent event) {}

	@Override
	public void focusLost(FocusEvent event)
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
			setForeground(Color.GRAY);
		}
	}
}
