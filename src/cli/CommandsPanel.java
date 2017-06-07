package cli;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import cli.exceptions.ProgramNotFoundException;
import cli.exceptions.StoppedProgramException;

@SuppressWarnings("serial")
final class CommandsPanel extends JPanel implements ActionListener, Runnable
{
	private static final int MARGIN = 10;

	private static final InfoTextArea COMMANDS_TEXT_AREA = new InfoTextArea();

	private final CLI_api api;

	private final JButton buttonCopy = new JButton(CLI_bundle.getPropertyDescription("CLI_window_copyClipboard"));
	private final JButton buttonStart = new JButton(CLI_bundle.getPropertyDescription("CLI_window_executeProgram"));

	private final String commandsStart;

	private Thread thread;

	CommandsPanel(final CLI_api api)
	{
		this.api = api;

		commandsStart = "java -jar " + api.getProjectName() + ' ';

		setLayout(new GridBagLayout());

		setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));

		setBackground(Color.LIGHT_GRAY);

		final JPanel buttonsPanel = createButtonsPanel();

		final GridBagConstraints constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;

		add(COMMANDS_TEXT_AREA, constraints);

		constraints.weightx = 0;

		add(buttonsPanel, constraints);

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
			CLI_output.getOutput().printError(error);
		}
	}

	void updateCommandsLine() throws IllegalArgumentException, IllegalAccessException, ProgramNotFoundException
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
			if (GUIconsole.getWindowsCount() < 2)
			{
				thread = new Thread(this);

				thread.start();
			}
			else
			{
				final String title = CLI_bundle.getPropertyDescription("CLI_window_multiWindowTitle");
				final String message = CLI_bundle.getPropertyDescription("CLI_window_multiWindowWarning");

				final JTextArea textArea = new JTextArea(message);
				textArea.setOpaque(false);

				textArea.setLineWrap(true);
				textArea.setWrapStyleWord(true);

				textArea.setSize(300, 200);

				final int choice = JOptionPane.showOptionDialog(null, textArea, title, JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE, null, null, null);

				if (choice == 0) // yes
				{
					thread = new Thread(this);

					thread.start();
				}
			}
		}
		else if (buttonCopy.equals(event.getSource()))
		{
			copyToClipboard();
		}
	}

	@Override
	public void run()
	{
		String commands = COMMANDS_TEXT_AREA.getText().replace(commandsStart, "");

		if (commands.isEmpty())
		{
			commands = " "; // to prevent StringIndexOutOfRangeException
		}

		final Date date = new Date();

		try
		{
			final String consoleTitle = api.getCurrentProgram().getName() + " - " + date;

			((WindowsManager) CLI_output.getOutput()).openNewConsole(consoleTitle);

			api.parse(commands);

			CLI_output.getOutput().println('\n' + CLI_bundle.getPropertyDescription("CLI_program_finished"));
		}
		catch (Exception error)
		{
			try
			{
				CLI_output.getOutput().println(error.getMessage());
			}
			catch (StoppedProgramException stop)
			{
				thread.interrupt();
			}
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
