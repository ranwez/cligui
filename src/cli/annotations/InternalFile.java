package cli.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code InternalFile} annotation can be used on {@code String} variables annotated with
 * {@code Parameter} to create a combo box in the GUI.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InternalFile
{
	/**
	 * @return an internal file path containing a list of filenames which will be stored in the
	 * combo box
	 */
	String value();
}
