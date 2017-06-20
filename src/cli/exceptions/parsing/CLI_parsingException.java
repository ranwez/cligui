package cli.exceptions.parsing;

import cli.exceptions.CLI_exception;

/**
 * This class generalizes parsing exceptions.
 */
@SuppressWarnings("serial")
public abstract class CLI_parsingException extends CLI_exception
{
	/**
	 * @param key a {@code String} key present in the bundle file
	 * @param objects any number of {@code Object} parameters whose values will be inserted into
	 * the description of the matching key
	 */
	protected CLI_parsingException(final String key, final Object... objects)
	{
		super(key, objects);
	}
}
