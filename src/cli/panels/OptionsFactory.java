package cli.panels;

import java.io.BufferedReader;
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
import cli.exceptions.StoppedProgramException;

public class OptionsFactory
{
	protected FocusablePanel createBooleanPanel(OptionBean optionBean) throws StoppedProgramException
	{
		return new BooleanPanel(optionBean);
	}

	protected FocusablePanel createCharPanel(OptionBean optionBean) throws StoppedProgramException
	{
		return new CharPanel(optionBean);
	}

	protected FocusablePanel createDoublePanel(OptionBean optionBean) throws StoppedProgramException
	{
		return new DoublePanel(optionBean);
	}

	protected FocusablePanel createFloatPanel(OptionBean optionBean) throws StoppedProgramException
	{
		return new FloatPanel(optionBean);
	}

	protected FocusablePanel createIntegerPanel(OptionBean optionBean) throws StoppedProgramException
	{
		return new IntegerPanel(optionBean);
	}

	protected FocusablePanel createStringPanel(OptionBean optionBean) throws StoppedProgramException
	{
		return new StringPanel(optionBean);
	}

	public final FocusablePanel createOptionPanel(OptionBean optionBean) throws StoppedProgramException
	{
		CLI_option option = optionBean.getOption();

		FocusablePanel optionPanel;

		Object optionType = option.getType();

		if (optionType.equals(boolean.class))
		{
			optionPanel = createBooleanPanel(optionBean);
		}
		else if (optionType.equals(char.class))
		{
			optionPanel = createCharPanel(optionBean);
		}
		else if (optionType.equals(double.class))
		{
			optionPanel = createDoublePanel(optionBean);
		}
		else if (optionType.equals(float.class))
		{
			optionPanel = createFloatPanel(optionBean);
		}
		else if (optionType.equals(int.class))
		{
			optionPanel = createIntegerPanel(optionBean);
		}
		else if (optionType.equals(String.class))
		{
			optionPanel = createInputOutputPanel(optionBean);
		}
		else
		{
			optionPanel = new UndefinedPanel(optionBean);
		}

		return optionPanel;
	}

	private FocusablePanel createInputOutputPanel(OptionBean optionBean) throws StoppedProgramException
	{
		FocusablePanel optionPanel;

		CLI_option option = optionBean.getOption();

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
			InternalFile internalFile = option.getAnnotation(InternalFile.class);

			if (internalFile != null)
			{
				String filepath = internalFile.value();

				String[] items = computeItems(filepath);

				optionPanel = new InternalFilePanel(optionBean, items);
			}
			else
			{
				optionPanel = createStringPanel(optionBean);
			}
		}

		return optionPanel;
	}

	private String[] computeItems(String filepath)
	{
		List<String> items = new ArrayList<String>();

		try
		{
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filepath);

			if (inputStream == null)
			{
				throw new FileNotFoundException("file not found : " + filepath);
			}

			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

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
