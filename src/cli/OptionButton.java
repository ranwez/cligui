package cli;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
final class OptionButton extends JButton
{
	private final List<JPanel> optionPanels = new ArrayList<JPanel>();

	OptionButton(String name)
	{
		super(name);
	}

	void updateColor(boolean isSelected)
	{
		if (isSelected)
		{
			setForeground(Color.GRAY);
		}
		else
		{
			setForeground(Color.BLACK);
		}
	}

	void addOptionPanel(JPanel optionPanel)
	{
		optionPanels.add(optionPanel);
	}

	JPanel getOptionPanel(int panelID)
	{
		return optionPanels.get(panelID);
	}

	int getOptionPanelsCount()
	{
		return optionPanels.size();
	}
}
