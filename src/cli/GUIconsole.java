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
public final class GUIconsole extends JFrame implements WindowListener
{
	private static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;

	private static int windowsCount;
	private static int windowPosition;

	private final InfoTextArea textArea = new InfoTextArea();

	private final JScrollPane scrollPanel = new JScrollPane(textArea);

	private boolean isDisposed;

	GUIconsole(final String title)
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

	public void print(final String text)
	{
		textArea.append(text);

		final JScrollBar scrollBar = scrollPanel.getVerticalScrollBar();

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
	public void windowOpened(final WindowEvent event) {}

	@Override
	public void windowClosing(final WindowEvent event) {}

	@Override
	public void windowClosed(final WindowEvent event)
	{
		windowsCount--;
	}

	@Override
	public void windowIconified(final WindowEvent event) {}

	@Override
	public void windowDeiconified(final WindowEvent event) {}

	@Override
	public void windowActivated(final WindowEvent event) {}

	@Override
	public void windowDeactivated(final WindowEvent event) {}
}
