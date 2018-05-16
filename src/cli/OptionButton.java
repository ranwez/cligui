package cli;

import static cli.CLI_bundleColor.GROUPS_TEXT_DISABLED_COLOR;
import static cli.CLI_bundleColor.GROUPS_TEXT_ENABLED_COLOR;
import static cli.CLI_bundleColor.GROUPS_BACK_DISABLED_COLOR;
import static cli.CLI_bundleColor.GROUPS_BACK_ENABLED_COLOR;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
final class OptionButton extends JButton
{
	private final List<JPanel> optionPanels = new ArrayList<JPanel>();

	OptionButton(final String name)
	{
		super(name);
	}

	void updateColor(final boolean isSelected)
	{
		if (isSelected)
		{
			setBackground(GROUPS_BACK_DISABLED_COLOR);
			setForeground(GROUPS_TEXT_DISABLED_COLOR);
		}
		else
		{
			setBackground(GROUPS_BACK_ENABLED_COLOR);
			setForeground(GROUPS_TEXT_ENABLED_COLOR);
		}
	}

	void addOptionPanel(final JPanel optionPanel)
	{
		optionPanels.add(optionPanel);
	}

	JPanel getOptionPanel(final int panelID)
	{
		return optionPanels.get(panelID);
	}

	int getOptionPanelsCount()
	{
		return optionPanels.size();
	}
}
