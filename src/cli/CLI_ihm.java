package cli;

import java.awt.Color;

public final class CLI_ihm
{
	private static final Color DEFAULT_COLOR = new Color(238, 238, 238);

	public static Color getCommandsColor()
	{
		return readBundleColor("CLI_ihm_commands");
	}

	public static Color getDescriptionColor()
	{
		return readBundleColor("CLI_ihm_description");
	}

	public static Color getButtonsBackgroundColor()
	{
		return readBundleColor("CLI_ihm_groups");
	}

	public static Color getOptionsColor()
	{
		return readBundleColor("CLI_ihm_options");
	}

	private static Color readBundleColor(final String bundleKey)
	{
		final String colorName = CLI_bundle.getPropertyDescription(bundleKey);

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

	private CLI_ihm() {}
}
