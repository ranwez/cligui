package cli;

import static cli.CLI_bundleColor.COMMAND_BUTTONS_COLOR;
import static cli.CLI_bundleColor.COMMAND_PANEL_COLOR;
import static cli.CLI_bundleColor.COMMAND_TEXT_COLOR;
import static cli.CLI_bundleMessage.WINDOW_COPY_CLIPBOARD;
import static cli.CLI_bundleMessage.WINDOW_EXECUTE_PROGRAM;
import static cli.CLI_bundleMessage.WINDOW_POPUP_MESSAGE;
import static cli.CLI_bundleMessage.WINDOW_POPUP_TITLE;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import cli.exceptions.StoppedProgramException;

@SuppressWarnings("serial")
final class CommandPanel extends JPanel implements ActionListener
{
	private static final String LOGO_FILEPATH = "cli_logo.png";

	private static final int LOGO_SIZE = 50;
	private static final int MARGIN = 10;

	private static final InfoTextArea COMMANDS_TEXT_AREA = new InfoTextArea();

	private static final JLabel LOGO_LABEL = createLogoLabel();

	private final CLI_api api;

	private final JButton buttonCopy = new JButton(WINDOW_COPY_CLIPBOARD);
	private final JButton buttonStart = new JButton(WINDOW_EXECUTE_PROGRAM);

	private final String commandsStart;

	private static JLabel createLogoLabel()
	{
		final File file = new File(LOGO_FILEPATH);

		JLabel logoLabel;

		if (file.exists())
		{
			final ImageIcon imageIcon = new ImageIcon(LOGO_FILEPATH);

			float height = imageIcon.getIconHeight();
			float width = imageIcon.getIconWidth();

			final float ratio = Math.max(width, height) / LOGO_SIZE;

			final int reducedHeight = (int) (height / ratio);
			final int reducedWidth = (int) (width / ratio);

			final Image image = imageIcon.getImage();

			final Image resizedImage = image.getScaledInstance(reducedWidth, reducedHeight, Image.SCALE_SMOOTH);

			final ImageIcon resizedImageIcon = new ImageIcon(resizedImage);

			logoLabel = new JLabel(resizedImageIcon, JLabel.CENTER);
		}
		else
		{
			logoLabel = new JLabel();
		}

		return logoLabel;
	}

	CommandPanel(final CLI_api api)
	{
		this.api = api;

		COMMANDS_TEXT_AREA.setEditable(true);
		COMMANDS_TEXT_AREA.setFocusable(true);
		COMMANDS_TEXT_AREA.setForeground(COMMAND_TEXT_COLOR);

		commandsStart = "java -jar " + api.getProjectName() + ' ';

		setLayout(new GridBagLayout());

		setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));

		setBackground(COMMAND_PANEL_COLOR);

		final JPanel buttonsPanel = createButtonsPanel();

		final GridBagConstraints constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 0, 0, 20);
		constraints.weightx = 1;

		add(LOGO_LABEL, constraints);

		constraints.insets = new Insets(0, 0, 0, 0);

		add(COMMANDS_TEXT_AREA, constraints);

		constraints.weightx = 0;

		add(buttonsPanel, constraints);

		buttonStart.setForeground(COMMAND_BUTTONS_COLOR);
		buttonCopy.setForeground(COMMAND_BUTTONS_COLOR);

		buttonStart.setFocusable(false);
		buttonCopy.setFocusable(false);

		buttonStart.addActionListener(this);
		buttonCopy.addActionListener(this);
	}

	private JPanel createButtonsPanel()
	{
		final JPanel buttonsPanel = new JPanel();

		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

		buttonsPanel.setOpaque(false);

		buttonsPanel.add(buttonStart);
		buttonsPanel.add(buttonCopy);

		return buttonsPanel;
	}

	void updateOptionAndCommandsLine(final CLI_option option, final String optionValue)
	{
		try
		{
			// TODO amélioration possible : modifier les valeurs une fois le programme exécuté

			if (option != null)
			{
				option.setParameter(optionValue);
			}

			updateCommandsLine();
		}
		catch (Exception error)
		{
			try
			{
				CLI_logger.logError(Level.SEVERE, error);
			}
			catch (StoppedProgramException stop)
			{
				stop.printStackTrace();
			}
		}
	}

	void updateCommandsLine() throws IllegalArgumentException, IllegalAccessException
	{
		final String programOptionName = api.getProgramOptionName();

		final StringBuilder builder = new StringBuilder(commandsStart);

		boolean notFirstOption = ! programOptionName.isEmpty();

		if (notFirstOption)
		{
			builder.append('-');
			builder.append(programOptionName);
			builder.append(' ');
			builder.append(api.getCurrentProgram().getName());
		}

		for (final CLI_option option : api.getCurrentProgram().getOptions())
		{
			final Object parameterValue = option.getValue();

			if (! parameterValue.equals(option.getDefaultValue()))
			{
				if (notFirstOption)
				{
					builder.append(' ');
				}
				else
				{
					notFirstOption = true;
				}

				builder.append('-');
				builder.append(option.getName());

				if (! parameterValue.getClass().equals(boolean.class))
				{
					builder.append(' ');
					builder.append(parameterValue);
				}
			}
		}

		COMMANDS_TEXT_AREA.setText(builder.toString());
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		if (buttonStart.equals(event.getSource()))
		{
			String commands = COMMANDS_TEXT_AREA.getText().replace(commandsStart, "");

			if (commands.isEmpty())
			{
				commands = " "; // to prevent StringIndexOutOfRangeException
			}

			if (GUIconsole.getWindowsCount() < 2)
			{
				new CLI_thread(api, commands);
			}
			else
			{
				final JTextArea textArea = new JTextArea(WINDOW_POPUP_MESSAGE);
				textArea.setOpaque(false);

				textArea.setLineWrap(true);
				textArea.setWrapStyleWord(true);

				textArea.setSize(300, 200);

				final int choice = JOptionPane.showOptionDialog(null, textArea, WINDOW_POPUP_TITLE, JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE, null, null, null);

				if (choice == 0) // yes
				{
					new CLI_thread(api, commands);
				}
			}
		}
		else if (buttonCopy.equals(event.getSource()))
		{
			copyToClipboard();
		}
	}

	private void copyToClipboard()
	{
		final String commandsLine = COMMANDS_TEXT_AREA.getText();

		final StringSelection selection = new StringSelection(commandsLine);

		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		clipboard.setContents(selection, null);
	}

	CLI_api getApi()
	{
		return api;
	}
}
