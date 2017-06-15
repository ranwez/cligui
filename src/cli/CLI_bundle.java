package cli;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class CLI_bundle
{
	private static final Properties PROPERTIES = readProperties("files/english.properties");

	private static String citation = "";

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
			error.printStackTrace();
		}

		return properties;
	}

	static String getCitation()
	{
		return citation;
	}

	static void setCitation(final String citation)
	{
		CLI_bundle.citation = citation;
	}

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

	private CLI_bundle() {}
}
