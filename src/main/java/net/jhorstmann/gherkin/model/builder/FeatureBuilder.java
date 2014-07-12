package net.jhorstmann.gherkin.model.builder;

import net.jhorstmann.gherkin.model.Feature;
import net.jhorstmann.gherkin.model.StepContainer;

import java.util.List;

public class FeatureBuilder {

    private String uri;
    private int lineNumber;
    private String keyword;
    private String description;
    private List<StepContainer> scenarios;

    public FeatureBuilder(String uri) {
        this.uri = uri;
    }

    public FeatureBuilder withLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }

    public FeatureBuilder withKeyword(String keyword) {
        this.keyword = keyword;
        return this;
    }

    public FeatureBuilder withDescription(String description) {
        this.description = description;
        return this;
    }


    public Feature build() {
        Feature feature = new Feature(uri, lineNumber, keyword, description);
        return feature;

    }
}
