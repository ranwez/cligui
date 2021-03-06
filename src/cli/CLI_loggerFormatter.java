package cli;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

final class CLI_loggerFormatter extends Formatter
{
	@Override
	public String format(final LogRecord record)
	{
		final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

		final String time = dateFormat.format(new Date());

		final StringBuilder builder = new StringBuilder();

		if (record.getLevel().intValue() < Level.INFO.intValue())
		{
			builder.append('[');
			builder.append(record.getLevel());
			builder.append("] ");
			builder.append(time);

			builder.append(' ');

			if (record.getLevel().intValue() < Level.CONFIG.intValue())
			{
				builder.append('[');
				builder.append(record.getSourceClassName());
				builder.append('.');
				builder.append(record.getSourceMethodName());
				builder.append("()] ");
			}

			builder.append(": ");
		}

		if (record.getParameters() == null)
		{
			builder.append(record.getMessage());
			if(record.getLevel()!=Level.INFO) // Let the user include the newLine when needed
				builder.append('\n');
		}
		else
		{
			builder.append(CLI_bundle.getPropertyDescription(record.getMessage(), record.getParameters()));
			builder.append('\n');
		}

		

		return builder.toString();
	}
}
