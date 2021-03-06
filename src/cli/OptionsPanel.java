package cli;

import static cli.CLI_bundleColor.DESCRIPTION_PANEL_COLOR;
import static cli.CLI_bundleColor.DESCRIPTION_TEXT_COLOR;
import static cli.CLI_bundleColor.OPTIONS_BORDER_COLOR;
import static cli.CLI_bundleColor.OPTIONS_NAME_NORMAL_COLOR;
import static cli.CLI_bundleColor.OPTIONS_NAME_REQUIRED_COLOR;
import static cli.CLI_bundleColor.OPTIONS_PANEL_COLOR;
import static cli.CLI_bundleKey.WINDOW_BUTTON_PREFIX;
import static cli.CLI_bundleKey.WINDOW_PROGRAM_DESCRIPTION;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import cli.annotations.InputFile;
import cli.annotations.OutputFile;
import cli.exceptions.StoppedProgramException;
import cli.exceptions.parsing.ProgramNotFoundException;
import cli.panels.OptionsFactory;

@SuppressWarnings("serial")
final class OptionsPanel extends JPanel implements ActionListener
{
	private static final String LAMP_FILEPATH = "lamp.gif";

	private static final int BIG_MARGIN = 20;
	private static final int DEFAULT_GROUP_ID = 1;
	private static final int NB_FIXED_TABS = 3;
	private static final int SMALL_MARGIN = 10;

	private static final List<Class<? extends Annotation>> ANNOTATIONS = createAnnotations();

	private static final InfoTextArea OPTION_TEXT_AREA = new InfoTextArea();

	private static final JLabel INFORMATION_LABEL = createInformationLabel();

	private final CommandPanel commandPanel;

	private final JPanel optionsPanel;

	private final GroupsPanel groupsPanel;

	private final OptionsFactory optionsFactory;

	private int selectedGroupID;

	private static List<Class<? extends Annotation>> createAnnotations()
	{
		List<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>();

		annotations.add(InputFile.class);
		annotations.add(OutputFile.class);

		return annotations;
	}

	private static JLabel createInformationLabel()
	{
		BufferedImage bufferedImage = null;

		try
		{
			bufferedImage = ImageIO.read(OptionsPanel.class.getResource(LAMP_FILEPATH));
		}
		catch (IOException error)
		{
			try
			{
				CLI_logger.getLogger().config(error.getMessage());
			}
			catch (StoppedProgramException stop) // should never happen
			{
				stop.printStackTrace();
			}
		}

		final Icon icon = new ImageIcon(bufferedImage);

		final JLabel informationLabel = new JLabel(icon);

		informationLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

		return informationLabel;
	}

	OptionsPanel(final CLI_api api, final OptionsFactory optionsFactory) throws ProgramNotFoundException, StoppedProgramException, IOException
	{
		this.optionsFactory = optionsFactory;

		for (final Class<? extends Annotation> annotation : optionsFactory.getAnnotations())
		{
			ANNOTATIONS.add(annotation);
		}

		OPTION_TEXT_AREA.setBorder(BorderFactory.createEmptyBorder(SMALL_MARGIN, SMALL_MARGIN, SMALL_MARGIN, SMALL_MARGIN));
		OPTION_TEXT_AREA.setForeground(DESCRIPTION_TEXT_COLOR);

		setLayout(new GridBagLayout());

		setBackground(DESCRIPTION_PANEL_COLOR);

		commandPanel = new CommandPanel(api);

		groupsPanel = new GroupsPanel();

		optionsPanel = createOptionsPanel();

		addComponents();

		updateGroupsPanel();
		updateOptionsPanel();
	}

	private JPanel createOptionsPanel()
	{
		final JPanel optionsPanel = new JPanel();

		optionsPanel.setBackground(OPTIONS_PANEL_COLOR);

		optionsPanel.setBorder(BorderFactory.createEmptyBorder(BIG_MARGIN, BIG_MARGIN, BIG_MARGIN, BIG_MARGIN));

		optionsPanel.setLayout(new GridBagLayout());

		return optionsPanel;
	}

	private void addComponents() throws IOException
	{
		final JPanel descriptionPanel = createDescriptionPanel();

		final JScrollPane scrollPane = new JScrollPane(optionsPanel);

		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		final GridBagConstraints constraints = new GridBagConstraints();

		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		//constraints.gridy++;
		//add(commandPanel, constraints);

		constraints.gridy++;
		add(descriptionPanel, constraints);

		constraints.gridy++;
		add(groupsPanel, constraints);

		int coordy=constraints.gridy;
		constraints.gridy=coordy+2;
		add(commandPanel, constraints);
		
		constraints.gridy=coordy+1;
		constraints.weighty = 1;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.BOTH;
		add(scrollPane, constraints);
		
		
	}

	private JPanel createDescriptionPanel() throws IOException
	{
		/*final JPanel descriptionPanelDoc = new JPanel();
		descriptionPanelDoc.setLayout(new BoxLayout(descriptionPanelDoc, BoxLayout.X_AXIS));
		descriptionPanelDoc.setOpaque(false);
		JTextField doc = new JTextField(CLI_bundleMessage.CMD_HELP_DOC);
		doc.setForeground(Color.red);
		doc.setBorder(null);
		descriptionPanelDoc.add(doc);
		*/
		JTextField doc = new JTextField(CLI_bundleMessage.CMD_HELP_DOC);
		doc.setForeground(cli.CLI_bundleColor.DESCRIPTION_TEXT_COLOR);
		doc.setBackground(cli.CLI_bundleColor.DESCRIPTION_PANEL_COLOR);
		doc.setFont(new Font(doc.getFont().getFontName(), Font.BOLD, doc.getFont().getSize()+2));
		doc.setBorder(null);
		
		final JPanel descriptionPanel = new JPanel();
		descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.Y_AXIS));
		descriptionPanel.setOpaque(false);
		descriptionPanel.add(doc);
		descriptionPanel.add(OPTION_TEXT_AREA);

		return descriptionPanel;
	}

	void updateGroupsPanel() throws ProgramNotFoundException, StoppedProgramException
	{
		groupsPanel.removeAll();

		addOptionButton("All");
		addOptionButton("Mandatory");
		addOptionButton("None");

		for (final Class<? extends Annotation> annotation : ANNOTATIONS)
		{
			addOptionButton(annotation.getSimpleName());
		}

		fillGroupsPanelOptions();

		selectedGroupID = DEFAULT_GROUP_ID;

		groupsPanel.updateVisibilities();
		groupsPanel.updateSelection(selectedGroupID);
	}

	private void addOptionButton(final String buttonType)
	{
		final String buttonName = CLI_bundle.getPropertyDescription(WINDOW_BUTTON_PREFIX + buttonType);

		final OptionButton optionButton = new OptionButton(buttonName);

		optionButton.addActionListener(this);

		groupsPanel.addOptionButton(optionButton);
	}

	private void fillGroupsPanelOptions() throws ProgramNotFoundException, StoppedProgramException
	{
		final CLI_program program = commandPanel.getApi().getCurrentProgram();

		for (final CLI_option option : program.getOptions())
		{
			if (! option.isHidden())
			{
				final JPanel optionPanel = createOptionPanel(option);

				groupsPanel.getOptionButton(0).addOptionPanel(optionPanel);

				if (option.isRequired())
				{
					groupsPanel.getOptionButton(1).addOptionPanel(optionPanel);
				}

				final int groupID = findOptionGroupID(option);

				groupsPanel.getOptionButton(groupID).addOptionPanel(optionPanel);
			}
		}
	}

	private JPanel createOptionPanel(final CLI_option option) throws StoppedProgramException
	{
		final JPanel externalOptionPanel = createExternalOptionPanel(option);

		final OptionBean optionBean = new OptionBean(commandPanel, option, OPTION_TEXT_AREA);

		final AbstractFocusablePanel internalOptionPanel = optionsFactory.createOptionPanel(optionBean);

		internalOptionPanel.setBackground(OPTIONS_PANEL_COLOR);

		externalOptionPanel.add(internalOptionPanel);

		return externalOptionPanel;
	}

	private JPanel createExternalOptionPanel(final CLI_option option)
	{
		final JPanel externalOptionPanel = new JPanel();

		externalOptionPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		externalOptionPanel.setLayout(new BoxLayout(externalOptionPanel, BoxLayout.X_AXIS));

		final TitledBorder titledBorder = BorderFactory.createTitledBorder(option.getName());

		titledBorder.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, OPTIONS_BORDER_COLOR));

		if (option.isRequired())
		{
			titledBorder.setTitleColor(OPTIONS_NAME_REQUIRED_COLOR);
		}
		else
		{
			titledBorder.setTitleColor(OPTIONS_NAME_NORMAL_COLOR);
		}

		externalOptionPanel.setBorder(titledBorder);

		return externalOptionPanel;
	}

	private int findOptionGroupID(final CLI_option option)
	{
		for (int groupsID = 0; groupsID < ANNOTATIONS.size(); groupsID++)
		{
			final Class<? extends Annotation> annotation = ANNOTATIONS.get(groupsID);

			if (option.getAnnotation(annotation) != null)
			{
				return groupsID + NB_FIXED_TABS;
			}
		}

		return NB_FIXED_TABS - 1;
	}

	void updateOptionsPanel()
	{
		optionsPanel.removeAll();

		final GridBagConstraints constraint = new GridBagConstraints();

		final OptionButton optionButton = groupsPanel.getOptionButton(selectedGroupID);

		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.weightx = 1;
		constraint.weighty = 1;

		for (int panelID = 0; panelID < optionButton.getOptionPanelsCount(); panelID++)
		{
			final JPanel optionPanel = optionButton.getOptionPanel(panelID);

			optionPanel.setBackground(OPTIONS_PANEL_COLOR);

			constraint.gridy++;

			optionsPanel.add(optionPanel, constraint);
		}

		repaint();
		validate();

		try
		{
			commandPanel.updateCommandsLine();

			updateProgramDescription();
		}
		catch (Exception error)
		{
			error.printStackTrace();
		}
	}

	private void updateProgramDescription() throws ProgramNotFoundException
	{
		final CLI_api api = commandPanel.getApi();

		final String description = CLI_bundle.getPropertyDescription(WINDOW_PROGRAM_DESCRIPTION, api.getCurrentProgram().getName(),
				api.getCurrentProgram().getDescription());

		OPTION_TEXT_AREA.setText(description);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		final OptionButton clickedOptionButton = (OptionButton) event.getSource();

		for (int id = 0; id < ANNOTATIONS.size() + NB_FIXED_TABS; id++)
		{
			final OptionButton optionButton = groupsPanel.getOptionButton(id);

			if (optionButton.equals(clickedOptionButton))
			{
				groupsPanel.updateSelection(id);

				selectedGroupID = id;

				break;
			}
		}

		updateOptionsPanel();
	}
}
