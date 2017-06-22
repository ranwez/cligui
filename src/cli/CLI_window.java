package cli;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import cli.exceptions.StoppedProgramException;
import cli.exceptions.parsing.ProgramNotFoundException;
import cli.panels.OptionsFactory;

/**
 * This class configures the window and menus of GUI.
 */
@SuppressWarnings("serial")
public final class CLI_window extends JFrame implements ActionListener
{
	private static final int WINDOW_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height * 2/3;
	private static final int WINDOW_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width * 3/5;

	private final CLI_api api;

	private final JMenuItem helpCitationMenuItem;
	private final JMenuItem helpTipsMenuItem = new JMenuItem(CLI_bundle.getPropertyDescription("CLI_window_helpTipsTitle"));

	private final OptionsPanel optionsPanel;

	private final String citationTitle;

	/**
	 * This constructor initializes the window parameters and menus with the default
	 * {@code OptionsFatory}.
	 * 
	 * @param api a {@code CLI_api} object with programs and options
	 * @throws Exception any parsing exception
	 */
	public CLI_window(final CLI_api api) throws Exception
	{
		this(api, new OptionsFactory());
	}

	/**
	 * This constructor initializes the window parameters and menus.
	 * 
	 * @param api a {@code CLI_api} object with programs and options
	 * @param optionsFactory a customized {@code OptionsFactory} used to create new options panels
	 * @throws Exception any parsing exception
	 */
	public CLI_window(final CLI_api api, final OptionsFactory optionsFactory) throws Exception
	{
		this.api = api;

		CLI_logger.setOutput(new WindowLogger());

		optionsPanel = new OptionsPanel(api, optionsFactory);

		final String title = api.getProjectName().replace(".jar", "");

		setTitle(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		citationTitle = CLI_bundle.getPropertyDescription("CLI_window_helpCitationTitle") + ' ' + title;

		helpCitationMenuItem = new JMenuItem(citationTitle);

		final JMenuBar menuBar = new JMenuBar();

		if (api.getPrograms().size() > 1)
		{
			menuBar.add(createProgramsMenu());
		}

		menuBar.add(createHelpMenu());

		setJMenuBar(menuBar);
		setContentPane(optionsPanel);

		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setLocationRelativeTo(null);

		setVisible(true);

		// the following line is used to remove the first auto focus, removing this line would cause the
		// description of the first program open to be replaced by the first option
		getContentPane().requestFocusInWindow();
	}

	private JMenu createProgramsMenu()
	{
		final JMenu programsMenu = new JMenu(CLI_bundle.getPropertyDescription("CLI_window_programs"));

		for (final CLI_program program : api.getPrograms())
		{
			final JMenuItem programsMenuItem = new JMenuItem(program.getName());

			programsMenuItem.addActionListener(this);

			programsMenu.add(programsMenuItem);
		}

		return programsMenu;
	}

	private JMenu createHelpMenu()
	{
		final JMenu helpMenu = new JMenu(CLI_bundle.getPropertyDescription("CLI_window_help"));

		helpCitationMenuItem.addActionListener(this);
		helpTipsMenuItem.addActionListener(this);

		helpMenu.add(helpCitationMenuItem);
		helpMenu.add(helpTipsMenuItem);

		return helpMenu;
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		if (helpTipsMenuItem.equals(event.getSource()))
		{
			final String title = CLI_bundle.getPropertyDescription("CLI_window_helpTipsTitle");
			final String message = CLI_bundle.getPropertyDescription("CLI_window_helpTipsContent");

			JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
		}
		else if (helpCitationMenuItem.equals(event.getSource()))
		{
			JOptionPane.showMessageDialog(this, CLI_bundle.getCitation(), citationTitle, JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			try
			{
				updateProgram(event);
			}
			catch (Exception stop) // should never happen
			{
				stop.printStackTrace();
			}
		}
	}

	private void updateProgram(final ActionEvent event) throws StoppedProgramException, ProgramNotFoundException
	{
		final String newProgramName = event.getActionCommand();

		try
		{
			api.getCurrentProgram().resetOptionsValues();

			api.setProgramName(newProgramName);
		}
		catch (Exception error)
		{
			CLI_logger.getLogger().info(error.getMessage());
		}

		optionsPanel.updateGroupsPanel();
		optionsPanel.updateOptionsPanel();
	}
}
