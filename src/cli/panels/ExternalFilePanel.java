package cli.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

import cli.AbstractFocusablePanel;
import cli.OptionBean;
import cli.OptionTextField;

@SuppressWarnings("serial")
final class ExternalFilePanel extends AbstractFocusablePanel implements ActionListener
{
	private static String defaultDirectory = "";

	private final boolean isSave;

	private final OptionTextField textField;

	ExternalFilePanel(final OptionBean optionBean, final boolean isSave)
	{
		super(optionBean);

		this.isSave = isSave;

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		final JButton browseButton = new JButton(UIManager.getIcon("FileView.directoryIcon"));

		browseButton.setFocusable(false);

		textField = new OptionTextField(optionBean);

		add(browseButton);
		add(textField);

		browseButton.addActionListener(this);

		textField.addFocusListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		JFileChooser chooser;

		if (defaultDirectory.isEmpty())
		{
			chooser = new JFileChooser();
		}
		else
		{
			chooser = new JFileChooser(defaultDirectory);
		}

		int action;

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

			final String filepath = chooser.getSelectedFile().getAbsolutePath();

			textField.setText(filepath);

			if (! filepath.isEmpty())
			{
				textField.setForeground(Color.BLACK);
			}

			updateOption(filepath);
		}
	}
}
