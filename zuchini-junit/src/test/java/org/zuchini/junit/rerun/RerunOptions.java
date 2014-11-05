package org.zuchini.junit.rerun;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RerunOptions {
    String DEFAULT_OUTPUT = "target/rerun.txt";

    String outputFile() default DEFAULT_OUTPUT;
}
