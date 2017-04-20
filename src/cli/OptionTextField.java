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
	private static final int DOUBLE_SPACE_ASCII_CODE = 160;

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

		if (option.getType().equals(double.class))
		{
			currentValue = removeDoubleSpaces(currentValue);
		}
		else if (option.getType().equals(int.class))
		{
			currentValue = currentValue.replace(",", "");
		}

		if (! option.getType().equals(float.class) || ! currentValue.isEmpty())
		{
			optionBean.getCommandsPanel().updateOptionAndCommandsLine(option, currentValue);
		}
	}

	private String removeDoubleSpaces(String doubleValue)
	{
		char digit;

		StringBuilder builder = new StringBuilder();

		for (int charID = 0; charID < doubleValue.length(); charID++)
		{
			digit = doubleValue.charAt(charID);

			if (digit != DOUBLE_SPACE_ASCII_CODE)
			{
				builder.append(digit);
			}
		}

		return builder.toString();
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
