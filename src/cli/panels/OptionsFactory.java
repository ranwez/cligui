package cli.panels;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import cli.CLI_option;
import cli.FocusablePanel;
import cli.OptionBean;
import cli.annotations.InputFile;
import cli.annotations.InternalFile;
import cli.annotations.OutputFile;

public class OptionsFactory
{
	public final FocusablePanel createOptionPanel(final OptionBean optionBean)
	{
		final CLI_option option = optionBean.getOption();

		final Object optionType = option.getType();

		FocusablePanel optionPanel;

		if (optionType.equals(boolean.class))
		{
			optionPanel = new BooleanPanel(optionBean);
		}
		else
		{
			optionPanel = createInputOutputPanel(optionBean);
		}

		return optionPanel;
	}

	private FocusablePanel createInputOutputPanel(final OptionBean optionBean)
	{
		final CLI_option option = optionBean.getOption();

		FocusablePanel optionPanel;

		if (option.getAnnotation(InputFile.class) != null)
		{
			optionPanel = new ExternalFilePanel(optionBean, false);
		}
		else if (option.getAnnotation(OutputFile.class) != null)
		{
			optionPanel = new ExternalFilePanel(optionBean, true);
		}
		else
		{
			final InternalFile internalFile = option.getAnnotation(InternalFile.class);

			if (internalFile != null)
			{
				final String filepath = internalFile.value().replace('/', File.separatorChar);

				optionPanel = createCustomComboBoxPanel(optionBean, filepath);
			}
			else
			{
				optionPanel = createCustomPanel(optionBean);
			}
		}

		return optionPanel;
	}

	protected FocusablePanel createCustomComboBoxPanel(final OptionBean optionBean, final String filepath)
	{
		final Object[] items = computeItems(filepath);

		return new InternalFilePanel(optionBean, items);
	}

	private String[] computeItems(final String filepath)
	{
		final List<String> items = new ArrayList<String>();

		try
		{
			final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filepath);

			if (inputStream == null)
			{
				throw new FileNotFoundException("file not found : " + filepath);
			}

			final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

			final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String line = bufferedReader.readLine();

			while (line != null)
			{
				items.add(line);

				line = bufferedReader.readLine();
			}
		}
		catch (Exception error)
		{
			error.printStackTrace();
		}

		return items.toArray(new String[items.size()]);
	}

	protected FocusablePanel createCustomPanel(final OptionBean optionBean)
	{
		return new StringPanel(optionBean);
	}

	/**
	 * This method can be redefined to add new categories in the window.
	 * 
	 * @return a list of annotations classes (used in programs with the {@code Parameter} annotations)
	 * to be added to the current existing list, these annotations will be used as categories in the window
	 */
	public List<Class<? extends Annotation>> getAnnotations()
	{
		return new ArrayList<Class<? extends Annotation>>();
	}
}
