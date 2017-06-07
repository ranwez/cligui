package cli.exceptions;

import cli.CLI_bundle;

/**
 * This class can be specialized to easily link an exception to the bundle.
 */
@SuppressWarnings("serial")
public abstract class CLI_exception extends Exception
{
	/**
	 * @param key a {@code String} key present in the bundle file
	 * @param objects any number of {@code Object} parameters whose values will be inserted into
	 * the description of the matching key
	 */
	protected CLI_exception(final String key, final Object... objects)
	{
		super(CLI_bundle.getPropertyDescription(key, objects) + '\n');
	}
}
