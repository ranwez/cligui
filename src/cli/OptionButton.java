package cli;

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
			setForeground(CLI_bundle.getBundleColor("CLI_ihm_groupsTextDisabled"));
		}
		else
		{
			setForeground(CLI_bundle.getBundleColor("CLI_ihm_groupsTextEnabled"));
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
