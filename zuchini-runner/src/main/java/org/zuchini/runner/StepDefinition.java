package org.zuchini.runner;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class StepDefinition {
    private final Pattern pattern;
    private final Method method;

    public StepDefinition(Pattern pattern, Method method) {
        this.pattern = pattern;
        this.method = method;
    }

    public StepDefinition(String pattern, Method method) {
        this(Pattern.compile(pattern), method);
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Method getMethod() {
        return method;
    }

}
