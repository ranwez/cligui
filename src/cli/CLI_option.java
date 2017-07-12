package cli;

import static cli.CLI_bundleMessage.OPTION_EMPTY_STRING;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import cli.annotations.Parameter;
import cli.exceptions.StoppedProgramException;

public final class CLI_option implements Comparable<CLI_option>
{
	private final Field field;

	private final Object instance;
	private final Object defaultValue;

	CLI_option(final Field field, final Object instance) throws IllegalAccessException, IllegalArgumentException
	{
		this.field = field;
		this.instance = instance;

		field.setAccessible(true);

		defaultValue = field.get(instance);

		field.setAccessible(false);
	}

	/**
	 * @param <T> the type of the annotation to query for and return if present
	 * @param cls annotationClass the {@code Class} object corresponding to the
	 * annotation type
	 * @return this element's annotation for the specified annotation type if
	 * present on this element, else null
	 * @throws NullPointerException if the given annotation class is null
	 */
	public <T extends Annotation> T getAnnotation(final Class<T> cls)
	{
		return field.getAnnotation(cls);
	}

	/**
	 * @return the option initial value, before commands parsing
	 */
	public Object getDefaultValue()
	{
		return defaultValue;
	}

	/**
	 * @return the option description located in the bundle using the option
	 * name as a key
	 */
	public String getDescription()
	{
		return CLI_bundle.getPropertyDescription(getName());
	}

	public String getName()
	{
		return getParameter().name();
	}

	private Parameter getParameter()
	{
		return field.getAnnotation(Parameter.class);
	}

	/**
	 * @return the option type class
	 */
	public Class<?> getType()
	{
		return field.getType();
	}

	void setParameter(final String parameter) throws IllegalAccessException, IllegalArgumentException
	{
		final Class<?> cls = field.getType();

		Object object;

		if (cls.equals(boolean.class))
		{
			object = Boolean.parseBoolean(parameter);
		}
		else if (cls.equals(char.class))
		{
			if (parameter.isEmpty())
			{
				object = '\0';
			}
			else
			{
				object = parameter.charAt(0);
			}
		}
		else if (cls.equals(double.class))
		{
			object = Double.parseDouble(parameter);
		}
		else if (cls.equals(float.class))
		{
			object = Float.parseFloat(parameter);
		}
		else if (cls.equals(int.class))
		{
			object = Integer.parseInt(parameter);
		}
		else
		{
			object = parameter;
		}

		setValue(object);
	}

	/**
	 * @return the option current value
	 * @throws IllegalAccessException if the option field is inaccessible
	 * @throws IllegalArgumentException if the option instance is not an
	 * instance of the option field
	 */
	public Object getValue() throws IllegalAccessException, IllegalArgumentException
	{
		field.setAccessible(true);

		final Object object = field.get(instance);

		field.setAccessible(false);

		return object;
	}

	private void setValue(final Object optionValue) throws IllegalAccessException, IllegalArgumentException
	{
		field.setAccessible(true);

		field.set(instance, optionValue);

		field.setAccessible(false);
	}

	void display() throws IllegalAccessException, IllegalArgumentException, StoppedProgramException
	{
		field.setAccessible(true);

		final Object realValue = field.get(instance);

		field.setAccessible(false);

		final String updatedRealValue = computeNoEmptyStringValue(realValue);

		char requirement;

		if (isRequired())
		{
			requirement = '*';
		}
		else
		{
			requirement = ' ';
		}

		final StringBuilder builder = new StringBuilder();

		builder.append("  -");
		builder.append(getName());
		builder.append(" : ");
		builder.append(getDescription());
		builder.append("\n    ");
		builder.append(requirement);
		builder.append(' ');
		builder.append(CLI_bundle.getPropertyDescription("CLI_option_value"));
		builder.append(" = ");
		builder.append(updatedRealValue);

		builder.append('\n');

		CLI_logger.getLogger().info(builder.toString());
	}

	public boolean isHidden()
	{
		return getParameter().hidden();
	}

	public boolean isRequired()
	{
		return getParameter().required();
	}

	private String computeNoEmptyStringValue(final Object objectValue)
	{
		String stringValue = objectValue.toString();

		if (stringValue.isEmpty())
		{
			final StringBuilder builder = new StringBuilder();

			builder.append('<');
			builder.append(OPTION_EMPTY_STRING);
			builder.append('>');

			stringValue = builder.toString();
		}

		return stringValue;
	}

	/**
	 * This method is used to simplify options sorting which can be done with
	 * {@code Collections.sort()} in the {@code CLI_program} constructor.
	 */
	@Override
	public int compareTo(final CLI_option option)
	{
		return getName().compareTo(option.getName());
	}

	void reset() throws IllegalAccessException, IllegalArgumentException
	{
		setValue(defaultValue);
	}
}
