package net.jhorstmann.gherkin.model;

import java.util.List;

public abstract class AbstractScenario {
    private int lineNumber;
    private List<String> tags;
    private String description;
    private List<Step> steps;

    protected AbstractScenario(int lineNumber, List<String> tags, String description, List<Step> steps) {
        this.lineNumber = lineNumber;
        this.tags = tags;
        this.description = description;
        this.steps = steps;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }

    public List<Step> getSteps() {
        return steps;
    }
}
