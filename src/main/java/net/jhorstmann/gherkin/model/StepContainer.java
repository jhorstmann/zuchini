package net.jhorstmann.gherkin.model;

import java.util.ArrayList;
import java.util.List;

public abstract class StepContainer implements Commented, Tagged, LocationAware {
    private final int lineNumber;
    private final Feature feature;
    private final String keyword;
    private final String description;
    private final List<String> tags = new ArrayList<>();
    private final List<String> comments = new ArrayList<>();
    private final List<Step> steps = new ArrayList<>();

    public StepContainer(Feature feature, int lineNumber, String keyword, String description) {
        this.feature = feature;
        this.lineNumber = lineNumber;
        this.keyword = keyword;
        this.description = description;
    }

    @Override
    public String getUri() {
        return feature.getUri();
    }

    public List<Step> getStepsIncludingBackground() {
        List<Step> steps = new ArrayList<>();
        for (Background background : feature.getBackground()) {
            steps.addAll(background.getSteps());
        }
        steps.addAll(this.steps);
        return steps;
    }

    public Feature getFeature() {
        return feature;
    }

    public List<Background> getBackground() {
        return feature.getBackground();
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getKeyword() {
        return keyword;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getComments() {
        return comments;
    }

    public String getDescription() {
        return description;
    }

    public List<Step> getSteps() {
        return steps;
    }


}
