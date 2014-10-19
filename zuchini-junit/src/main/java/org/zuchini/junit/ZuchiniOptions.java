package org.zuchini.junit;

import org.zuchini.runner.ScenarioScope;
import org.zuchini.runner.ThreadLocalScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ZuchiniOptions {
    String[] featurePackages();
    String[] stepDefinitionPackages();
    Class<? extends ScenarioScope> scope() default ThreadLocalScope.class;
}
