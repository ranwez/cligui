package cli.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

import cli.FocusablePanel;
import cli.OptionBean;
import cli.OptionTextField;

@SuppressWarnings("serial")
final class ExternalFilePanel extends FocusablePanel implements ActionListener
{
	private static String defaultDirectory = "";

	private final boolean isSave;

	private final OptionTextField textField;

	ExternalFilePanel(OptionBean optionBean, boolean isSave)
	{
		super(optionBean);

		this.isSave = isSave;

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		JButton browseButton = new JButton(UIManager.getIcon("FileView.directoryIcon"));

		browseButton.setFocusable(false);

		textField = new OptionTextField(optionBean);

		add(browseButton);
		add(textField);

		browseButton.addActionListener(this);

		textField.addFocusListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		int action;

		JFileChooser chooser;

		if (defaultDirectory.isEmpty())
		{
			chooser = new JFileChooser();
		}
		else
		{
			chooser = new JFileChooser(defaultDirectory);
		}

		if (isSave)
		{
			action = chooser.showSaveDialog(this);
		}
		else
		{
			action = chooser.showOpenDialog(this);
		}

		if (action == JFileChooser.APPROVE_OPTION)
		{
			defaultDirectory = chooser.getCurrentDirectory().getPath();

			String filepath = chooser.getSelectedFile().getAbsolutePath();

			textField.setText(filepath);

			if (! filepath.isEmpty())
			{
				textField.setForeground(Color.BLACK);
			}

			updateOption(filepath);
		}
	}
}
