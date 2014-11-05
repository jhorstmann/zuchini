package org.zuchini.junit;

import org.junit.runner.notification.RunListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ZuchiniOptions {
    String[] featurePackages();
    String[] stepDefinitionPackages();
    boolean reportIndividualSteps() default false;
    Class<? extends RunListener>[] listeners() default {};
}
