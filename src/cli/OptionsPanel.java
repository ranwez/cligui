package cli;

import java.awt.Color;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import cli.annotations.InputFile;
import cli.annotations.OutputFile;
import cli.exceptions.ProgramNotFoundException;
import cli.exceptions.StoppedProgramException;
import cli.panels.OptionsFactory;

@SuppressWarnings("serial")
final class OptionsPanel extends JPanel implements ActionListener
{
	private static final int BIG_MARGIN = 20;
	private static final int SMALL_MARGIN = 10;

	private static final List<Class<? extends Annotation>> ANNOTATIONS = createAnnotations();

	private static final InfoTextArea OPTION_TEXT_AREA = new InfoTextArea();

	private static List<Class<? extends Annotation>> createAnnotations()
	{
		List<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>();

		annotations.add(InputFile.class);
		annotations.add(OutputFile.class);

		return annotations;
	}

	private final CommandsPanel commandsPanel;

	private final JPanel optionsPanel;

	private final GroupsPanel groupsPanel;

	private final OptionsFactory optionsFactory;

	private int selectedGroupID;

	OptionsPanel(CLI_api api, OptionsFactory optionsFactory) throws ProgramNotFoundException, StoppedProgramException, IOException
	{
		this.optionsFactory = optionsFactory;

		for (Class<? extends Annotation> annotation : optionsFactory.getAnnotations())
		{
			ANNOTATIONS.add(annotation);
		}

		OPTION_TEXT_AREA.setBorder(BorderFactory.createEmptyBorder(SMALL_MARGIN, SMALL_MARGIN, SMALL_MARGIN, SMALL_MARGIN));

		setLayout(new GridBagLayout());

		setBackground(Color.WHITE);

		commandsPanel = new CommandsPanel(api);

		groupsPanel = new GroupsPanel();

		optionsPanel = new JPanel();
		optionsPanel.setBorder(BorderFactory.createEmptyBorder(BIG_MARGIN, BIG_MARGIN, BIG_MARGIN, BIG_MARGIN));
		optionsPanel.setLayout(new GridBagLayout());

		addComponents();

		updateGroupsPanel();
		updateOptionsPanel();
	}

	private void addComponents() throws IOException
	{
		JPanel descriptionPanel = createDescriptionPanel();

		JScrollPane scrollPane = new JScrollPane(optionsPanel);

		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		GridBagConstraints constraints = new GridBagConstraints();

		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridy++;

		add(commandsPanel, constraints);

		constraints.gridy++;

		add(descriptionPanel, constraints);

		constraints.gridy++;

		add(groupsPanel, constraints);

		constraints.gridy++;
		constraints.weighty = 1;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.BOTH;

		add(scrollPane, constraints);
	}

	private JPanel createDescriptionPanel() throws IOException
	{
		BufferedImage bufferedImage = ImageIO.read(getClass().getResource("lamp.gif"));

		Icon icon = new ImageIcon(bufferedImage);

		JLabel informationLabel = new JLabel(icon);

		informationLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

		JPanel descriptionPanel = new JPanel();

		descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.X_AXIS));

		descriptionPanel.setOpaque(false);

		descriptionPanel.add(informationLabel);
		descriptionPanel.add(OPTION_TEXT_AREA);

		return descriptionPanel;
	}

	void updateGroupsPanel() throws ProgramNotFoundException, StoppedProgramException
	{
		groupsPanel.removeAll();

		addOptionButton("All");
		addOptionButton("None");

		for (Class<? extends Annotation> annotation : ANNOTATIONS)
		{
			addOptionButton(annotation.getSimpleName());
		}

		fillGroupsPanelOptions();

		groupsPanel.updateVisibilities();

		selectedGroupID = 0;

		for (int id = 0; id < ANNOTATIONS.size() + 2; id++)
		{
			if (groupsPanel.getOptionButton(id).isVisible())
			{
				selectedGroupID = id;

				break;
			}
		}

		groupsPanel.updateSelection(selectedGroupID);
	}

	private void addOptionButton(String buttonType)
	{
		String buttonName = CLI_bundle.getPropertyDescription("CLI_window_button" + buttonType);

		OptionButton optionButton = new OptionButton(buttonName);
		optionButton.addActionListener(this);

		groupsPanel.addOptionButton(optionButton);
	}

	private void fillGroupsPanelOptions() throws ProgramNotFoundException, StoppedProgramException
	{
		int groupID;

		CLI_program program = commandsPanel.getApi().getCurrentProgram();

		JPanel optionPanel;

		for (CLI_option option : program.getOptions())
		{
			if (! option.isHidden())
			{
				optionPanel = createOptionPanel(option);

				groupID = findOptionGroupID(option);

				groupsPanel.getOptionButton(0).addOptionPanel(optionPanel);
				groupsPanel.getOptionButton(groupID).addOptionPanel(optionPanel);
			}
		}
	}

	private JPanel createOptionPanel(CLI_option option) throws StoppedProgramException
	{
		JPanel externalOptionPanel = createExternalOptionPanel(option);

		OptionBean optionBean = new OptionBean(commandsPanel, option, OPTION_TEXT_AREA);

		FocusablePanel internalOptionPanel = optionsFactory.createOptionPanel(optionBean);

		externalOptionPanel.add(internalOptionPanel);

		return externalOptionPanel;
	}

	private JPanel createExternalOptionPanel(CLI_option option)
	{
		JPanel externalOptionPanel = new JPanel();

		externalOptionPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		externalOptionPanel.setLayout(new BoxLayout(externalOptionPanel, BoxLayout.X_AXIS));

		TitledBorder titledBorder = BorderFactory.createTitledBorder(option.getName());

		if (option.isRequired())
		{
			titledBorder.setTitleColor(Color.RED);
		}

		externalOptionPanel.setBorder(titledBorder);

		return externalOptionPanel;
	}

	private int findOptionGroupID(CLI_option option)
	{
		Class<? extends Annotation> annotation;

		for (int groupsID = 0; groupsID < ANNOTATIONS.size(); groupsID++)
		{
			annotation = ANNOTATIONS.get(groupsID);

			if (option.getAnnotation(annotation) != null)
			{
				return groupsID + 2;
			}
		}

		return 1;
	}

	void updateOptionsPanel()
	{
		optionsPanel.removeAll();

		GridBagConstraints constraint = new GridBagConstraints();

		JPanel optionPanel;

		OptionButton optionButton = groupsPanel.getOptionButton(selectedGroupID);

		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.weightx = 1;
		constraint.weighty = 1;

		for (int panelID = 0; panelID < optionButton.getOptionPanelsCount(); panelID++)
		{
			optionPanel = optionButton.getOptionPanel(panelID);

			constraint.gridy++;

			optionsPanel.add(optionPanel, constraint);
		}

		repaint();
		validate();

		commandsPanel.initOptionAndCommandsLine();

		try
		{
			updateProgramDescription();
		}
		catch (ProgramNotFoundException error)
		{
			error.printStackTrace();
		}
	}

	private void updateProgramDescription() throws ProgramNotFoundException
	{
		CLI_api api = commandsPanel.getApi();

		String description = CLI_bundle.getPropertyDescription("CLI_window_programDescription", api.getCurrentProgram().getDescription());

		OPTION_TEXT_AREA.setText(description);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		OptionButton clickedOptionButton = (OptionButton) event.getSource();
		OptionButton optionButton;

		for (int id = 0; id < ANNOTATIONS.size() + 2; id++)
		{
			optionButton = groupsPanel.getOptionButton(id);

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
