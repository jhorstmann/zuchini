package org.zuchini.gherkin.model;

public class Background extends StepContainer {
    public Background(Feature feature, int lineNumber, String keyword, String description) {
        super(feature, lineNumber, keyword, description);
    }


    @Override
    public String toString() {
        return "Background@" + getUri() + ":" + getLineNumber() + "[" + getDescription() + "]";
    }
}
