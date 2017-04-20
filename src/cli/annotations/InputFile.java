package cli.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code InputFile} annotation can be used on {@code String} variables annotated with
 * {@code Parameter} to define the file format to be used in galaxy XML files creation, it will
 * also replace its corresponding panel in the GUI with a file chooser.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InputFile
{
	String value();
}
