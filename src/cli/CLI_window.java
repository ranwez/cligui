package cli;

import static cli.CLI_bundleKey.WINDOW_MENU_CITATION_TITLE;
import static cli.CLI_bundleMessage.WINDOW_MENU_ABOUT_MESSAGE;
import static cli.CLI_bundleMessage.WINDOW_MENU_ABOUT_TITLE;
import static cli.CLI_bundleMessage.WINDOW_MENU_CITATION_MESSAGE;
import static cli.CLI_bundleMessage.FONT_NAME;
import static cli.CLI_bundleMessage.FONT_SIZE;
import static cli.CLI_bundleMessage.WINDOW_MENU;
import static cli.CLI_bundleMessage.WINDOW_MENU_TIPS_MESSAGE;
import static cli.CLI_bundleMessage.WINDOW_MENU_TIPS_TITLE;
import static cli.CLI_bundleMessage.WINDOW_PROGRAMS;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

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

	private final JMenuItem helpAboutMenuItem;
	private final JMenuItem helpCitationMenuItem;
	private final JMenuItem helpTipsMenuItem;

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

		loadFont();

		helpAboutMenuItem = new JMenuItem(WINDOW_MENU_ABOUT_TITLE);
		helpTipsMenuItem = new JMenuItem(WINDOW_MENU_TIPS_TITLE);

		CLI_logger.setWindowOutput();

		optionsPanel = new OptionsPanel(api, optionsFactory);

		final String title = api.getProjectName().replace(".jar", "");

		setTitle(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		citationTitle = CLI_bundle.getPropertyDescription(WINDOW_MENU_CITATION_TITLE, title);

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

	private void loadFont()
	{
		// UIManager keys : http://thebadprogrammer.com/swing-uimanager-keys/

		// note : you can CTRL + click on the link to open it in Eclipse

		final Font font = new Font(FONT_NAME, Font.PLAIN, FONT_SIZE);

		UIManager.put("Button.font", font);
		UIManager.put("ComboBox.font", font);
		UIManager.put("FormattedTextField.font", font);
		UIManager.put("Menu.font", font);
		UIManager.put("MenuItem.font", font);
		UIManager.put("OptionPane.buttonFont", font);
		UIManager.put("OptionPane.messageFont", font);
		UIManager.put("TextArea.font", font);
		UIManager.put("TitledBorder.font", font);
	}

	private JMenu createProgramsMenu()
	{
		final JMenu programsMenu = new JMenu(WINDOW_PROGRAMS);

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
		final JMenu helpMenu = new JMenu(WINDOW_MENU);

		helpAboutMenuItem.addActionListener(this);
		helpCitationMenuItem.addActionListener(this);
		helpTipsMenuItem.addActionListener(this);

		if (! WINDOW_MENU_CITATION_MESSAGE.isEmpty())
		{
			helpMenu.add(helpCitationMenuItem);
		}

		helpMenu.add(helpTipsMenuItem);

		if (! WINDOW_MENU_ABOUT_MESSAGE.isEmpty())
		{
			helpMenu.add(helpAboutMenuItem);
		}

		return helpMenu;
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		if (helpTipsMenuItem.equals(event.getSource()))
		{
			JOptionPane.showMessageDialog(this, WINDOW_MENU_TIPS_MESSAGE, WINDOW_MENU_TIPS_TITLE, JOptionPane.INFORMATION_MESSAGE);
		}
		else if (helpCitationMenuItem.equals(event.getSource()))
		{
			JOptionPane.showMessageDialog(this, WINDOW_MENU_CITATION_MESSAGE, citationTitle, JOptionPane.INFORMATION_MESSAGE);
		}
		else if (helpAboutMenuItem.equals(event.getSource()))
		{
			JOptionPane.showMessageDialog(this, WINDOW_MENU_ABOUT_MESSAGE, WINDOW_MENU_ABOUT_TITLE, JOptionPane.INFORMATION_MESSAGE);
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
			CLI_logger.logError(Level.SEVERE, error);
		}

		optionsPanel.updateGroupsPanel();
		optionsPanel.updateOptionsPanel();
	}
}
