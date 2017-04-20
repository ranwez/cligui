package cli.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Variables annotated with {@code Parameter} will be used as {@code CLI_option} objects and added
 * to the program they belong.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Parameter
{
	/**
	 * @return the name of the {@code CLI_option} will be used in the commands line and must be
	 * present in the bundle for description
	 */
	String name();

	/**
	 * @return if this option is true, then any attempt to run the program without the
	 * required {@code CLI_option} will throw an error
	 */
	boolean required() default false;

	/**
	 * @return this option can be used to hide a {@code CLI_option} from the options list without
	 * disabling it
	 */
	boolean hidden() default false;

	/**
	 * @return the enumeration option allows to convert an enumeration class into a
	 * combo box on the GUI and should only be used on integer variables, the combo box will contain
	 * all enumerations names and the commands line their respective position in the list as integers
	 */
	Class<?> enumeration() default Object.class;
}
