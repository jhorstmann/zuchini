package org.zuchini.model;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class Scenario extends StepContainer {
    private static final Map<String, String> EMPTY_PARAMS = emptyMap();
    private final Map<String, String> parameters;

    public Scenario(Feature feature, int lineNumber, String keyword, String description) {
        this(feature, lineNumber, keyword, description, EMPTY_PARAMS);
    }

    public Scenario(Feature feature, int lineNumber, String keyword, String description, Map<String, String> parameters) {
        super(feature, lineNumber, keyword, description + (parameters.isEmpty() ? "" : " " + parameters));
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "Scenario@" + getUri() + ":" + getLineNumber() + "[" + getDescription() + "]";
    }
}
