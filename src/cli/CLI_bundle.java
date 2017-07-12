package cli;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

import cli.exceptions.StoppedProgramException;

public final class CLI_bundle
{
	private static final Color DEFAULT_COLOR = new Color(238, 238, 238);

	private static final Properties PROPERTIES = readProperties("files/bundle/english.properties");

	/**
	 * This method will look for a description in the bundle file that matches a {@code String}
	 * key and will replace all '@' symbols with the defined values.
	 * 
	 * @param key a {@code String} key present in the bundle file
	 * @param objects any number of {@code Object} parameters whose values will be inserted into
	 * the description of the matching key
	 * @return a description that matches the key with the objects values
	 */
	public static String getPropertyDescription(final String key, final Object... objects)
	{
		String description = PROPERTIES.getProperty(key);

		for (int id = 0; id < objects.length; id++)
		{
			final String value = String.valueOf(objects[id]);

			final String code = '@' + String.valueOf(id + 1);

			description = description.replace(code, value);
		}

		return description;
	}

	static void addProperties(final String filepath)
	{
		final Properties newProperties = readProperties(filepath);

		PROPERTIES.putAll(newProperties);
	}

	private static Properties readProperties(final String filepath)
	{
		Properties properties = null;

		try
		{
			properties = new Properties();

			final InputStream inputStream = CLI_bundle.class.getClassLoader().getResourceAsStream(filepath);

			if (inputStream == null)
			{
				throw new FileNotFoundException("file not found : " + filepath);
			}

			properties.load(inputStream);

			inputStream.close();
		}
		catch (IOException error)
		{
			try
			{
				CLI_logger.logError(Level.CONFIG, error);
			}
			catch (StoppedProgramException stop)
			{
				stop.printStackTrace();
			}
		}

		return properties;
	}

	static String getCitation()
	{
		return getPropertyDescription("CLI_citation");
	}

	static Color getBundleColor(final String bundleKey)
	{
		final String colorName = getPropertyDescription(bundleKey);

		Color color;

		try
		{
			color = (Color) Color.class.getField(colorName).get(null);
		}
		catch (Exception error)
		{
			if (colorName.charAt(0) == '#')
			{
				color = hexaToColor(colorName);
			}
			else
			{
				color = DEFAULT_COLOR;
			}
		}

		return color;
	}

	private static Color hexaToColor(final String colorHexaCode)
	{
		final String hexRed = colorHexaCode.substring(1, 3);
		final String hexGreen = colorHexaCode.substring(3, 5);
		final String hexBlue = colorHexaCode.substring(5, 7);

		final int red = Integer.valueOf(hexRed, 16);
		final int green = Integer.valueOf(hexGreen, 16);
		final int blue = Integer.valueOf(hexBlue, 16);

		return new Color(red, green, blue);
	}

	private CLI_bundle() {}
}
