package cli;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

@SuppressWarnings("serial")
final class GroupsPanel extends JPanel
{
	private static final GridBagConstraints CONSTRAINTS = new GridBagConstraints();

	private final List<OptionButton> optionButtons = new ArrayList<OptionButton>();

	GroupsPanel()
	{
		setLayout(new GridBagLayout());

		CONSTRAINTS.fill = GridBagConstraints.HORIZONTAL;
		CONSTRAINTS.weightx = 1;
	}

	void addOptionButton(final OptionButton optionButton)
	{
		CONSTRAINTS.gridx++;

		add(optionButton, CONSTRAINTS);

		optionButtons.add(optionButton);
	}

	OptionButton getOptionButton(final int optionButtonID)
	{
		return optionButtons.get(optionButtonID);
	}

	void updateSelection(final int selectedOptionButtonID)
	{
		for (int optionButtonID = 0; optionButtonID < optionButtons.size(); optionButtonID++)
		{
			final boolean isSelected = optionButtonID == selectedOptionButtonID;

			final OptionButton optionButton = optionButtons.get(optionButtonID);

			optionButton.updateColor(isSelected);
		}
	}

	void updateVisibilities()
	{
		for (final OptionButton optionButton : optionButtons)
		{
			if (optionButton.getOptionPanelsCount() == 0)
			{
				optionButton.setVisible(false);
			}
		}
	}

	@Override
	public void removeAll()
	{
		super.removeAll();

		optionButtons.clear();
	}
}
