package cli;

import java.awt.Color;

final class CLI_bundleColor
{
	private static final Color DEFAULT_COLOR = new Color(238, 238, 238);

	static final Color COMMAND_PANEL_COLOR =					readBundleColor("CLI_ihm_commandPanel");
	static final Color COMMAND_TEXT_COLOR =						readBundleColor("CLI_ihm_commandText");
	static final Color COMMAND_BUTTONS_COLOR =					readBundleColor("CLI_ihm_commandButtons");

	static final Color DESCRIPTION_PANEL_COLOR =				readBundleColor("CLI_ihm_descriptionPanel");
	static final Color DESCRIPTION_TEXT_COLOR =					readBundleColor("CLI_ihm_descriptionText");

	static final Color GROUPS_PANEL_COLOR =						readBundleColor("CLI_ihm_groupsPanel");
	static final Color GROUPS_TEXT_DISABLED_COLOR =				readBundleColor("CLI_ihm_groupsTextDisabled");
	static final Color GROUPS_TEXT_ENABLED_COLOR =				readBundleColor("CLI_ihm_groupsTextEnabled");
	static final Color GROUPS_BACK_ENABLED_COLOR =				readBundleColor("CLI_ihm_groupsBackgroundEnabled");
	static final Color GROUPS_BACK_DISABLED_COLOR =				readBundleColor("CLI_ihm_groupsBackgroundDisabled");
	
	static final Color OPTIONS_AREA_BACKGROUND_COLOR =			readBundleColor("CLI_ihm_optionsAreaBackground");
	static final Color OPTIONS_AREA_NORMAL_COLOR =				readBundleColor("CLI_ihm_optionsAreaNormal");
	static final Color OPTIONS_AREA_HIDDEN_COLOR =				readBundleColor("CLI_ihm_optionsAreaHidden");

	static final Color OPTIONS_BORDER_COLOR =					readBundleColor("CLI_ihm_optionsBorder");
	static final Color OPTIONS_NAME_NORMAL_COLOR =				readBundleColor("CLI_ihm_optionsNameNormal");
	static final Color OPTIONS_NAME_REQUIRED_COLOR =			readBundleColor("CLI_ihm_optionsNameRequired");
	static final Color OPTIONS_PANEL_COLOR =					readBundleColor("CLI_ihm_optionsPanel");

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

	private CLI_bundleColor() {}
}
