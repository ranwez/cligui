package cli.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Some programs may need to use the same {@code Parameter} variables, you can group these common
 * variables in another class and create an annotated object pointing to this class with Delegate.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Delegate {}
