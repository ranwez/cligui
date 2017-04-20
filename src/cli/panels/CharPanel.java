package cli.panels;

import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import cli.FocusablePanel;
import cli.OptionBean;
import cli.OptionTextField;

@SuppressWarnings("serial")
class CharPanel extends FocusablePanel implements KeyListener
{
	private final OptionTextField textField;

	CharPanel(OptionBean optionBean)
	{
		super(optionBean);

		setLayout(new FlowLayout(FlowLayout.LEFT));

		textField = new OptionTextField(optionBean);

		textField.setColumns(1);

		textField.addKeyListener(this);
		textField.addFocusListener(this);

		add(textField);
	}

	@Override
	public void keyTyped(KeyEvent event)
	{
		checkTypedChar(event);
	}

	@Override
	public void keyPressed(KeyEvent event)
	{
		checkTypedChar(event);
	}

	@Override
	public void keyReleased(KeyEvent event) {}

	private void checkTypedChar(KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE || event.getKeyCode() == KeyEvent.VK_DELETE
				|| event.getKeyCode() == KeyEvent.VK_LEFT || event.getKeyCode() == KeyEvent.VK_RIGHT
				|| event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_DOWN)
		{
			return;
		}

		if (textField.getText().length() > 0)
		{
			textField.setText("");
		}
	}
}
