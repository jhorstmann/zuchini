package cucumber.api.java.en;

import cucumber.runtime.java.StepDefAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@StepDefAnnotation
public @interface Given {
    /**
     * @return a regular expression
     */
    String value();

    /**
     * @return max amount of milliseconds this is allowed to run for. 0 (default) means no restriction.
     */
    long timeout() default 0;
}
