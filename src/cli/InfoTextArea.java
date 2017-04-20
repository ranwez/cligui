package cli;

import javax.swing.JTextArea;

@SuppressWarnings("serial")
final class InfoTextArea extends JTextArea
{
	InfoTextArea()
	{
		setEditable(false);
		setFocusable(false);
		setLineWrap(true);
		setOpaque(false);
		setWrapStyleWord(true);
	}
}
