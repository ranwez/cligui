package cli;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public final class GUIconsole extends JFrame implements WindowListener
{
	private static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	private static final int WINDOWS_SPACE = 40;

	private static int windowsCount;
	private static int windowPosition;

	private final InfoTextArea textArea = new InfoTextArea();

	private final JScrollPane scrollPanel = new JScrollPane(textArea);

	private final Logger logger;

	private boolean isDisposed;

	GUIconsole(final String title)
	{
		logger = Logger.getLogger(title);

		logger.addHandler(new CLI_windowHandler(this));

		logger.setUseParentHandlers(false);

		windowsCount++;

		textArea.setMargin(new Insets(5, 5, 5, 5));

		setBackground(Color.WHITE);

		setTitle(title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLocation(windowPosition % SCREEN_WIDTH, windowPosition % SCREEN_HEIGHT);

		windowPosition += WINDOWS_SPACE;

		setContentPane(scrollPanel);

		setSize(800, 500);
		setVisible(true);

		addWindowListener(this);
	}

	static int getWindowsCount()
	{
		return windowsCount;
	}

	public Logger getLogger()
	{
		return logger;
	}

	public void print(final String text)
	{
		logger.info(text);

		final JScrollBar scrollBar = scrollPanel.getVerticalScrollBar();

		scrollBar.setValue(scrollBar.getMaximum());
	}

	public InfoTextArea getTextArea()
	{
		return textArea;
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

		windowPosition -= WINDOWS_SPACE;
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
