package org.zuchini.annotations;

import cucumber.runtime.java.StepDefAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@StepAnnotation
@StepDefAnnotation
public @interface Given {
    public String value();
}
