package cli;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
final class GUIconsole extends JFrame implements WindowListener
{
	private static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;

	private static int windowsCount;
	private static int windowPosition;

	private final InfoTextArea textArea = new InfoTextArea();

	private final JScrollPane scrollPanel = new JScrollPane(textArea);

	private boolean isDisposed;

	GUIconsole(String title)
	{
		windowsCount++;

		textArea.setMargin(new Insets(5, 5, 5, 5));

		setBackground(Color.WHITE);

		setTitle(title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLocation(windowPosition % SCREEN_WIDTH, windowPosition % SCREEN_HEIGHT);

		windowPosition += 40;

		setContentPane(scrollPanel);

		setSize(800, 500);
		setVisible(true);

		addWindowListener(this);
	}

	static int getWindowsCount()
	{
		return windowsCount;
	}

	void print(String text)
	{
		textArea.append(text);

		JScrollBar scrollBar = scrollPanel.getVerticalScrollBar();

		scrollBar.setValue(scrollBar.getMaximum());
	}

	@Override
	public void dispose()
	{
		super.dispose();

		isDisposed = true;
	}

	boolean isDisposed()
	{
		return isDisposed;
	}

	@Override
	public void windowOpened(WindowEvent event) {}

	@Override
	public void windowClosing(WindowEvent event) {}

	@Override
	public void windowClosed(WindowEvent event)
	{
		windowsCount--;
	}

	@Override
	public void windowIconified(WindowEvent event) {}

	@Override
	public void windowDeiconified(WindowEvent event) {}

	@Override
	public void windowActivated(WindowEvent event) {}

	@Override
	public void windowDeactivated(WindowEvent event) {}
}
