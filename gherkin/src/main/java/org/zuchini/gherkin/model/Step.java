package org.zuchini.gherkin.model;

import java.util.ArrayList;
import java.util.List;

public class Step implements Commented, Tagged, LocationAware {
    private final StepContainer stepContainer;
    private final int lineNumber;
    private final String keyword;
    private final String description;
    private final List<String> tags = new ArrayList<>();
    private final List<String> comments = new ArrayList<>();
    private final List<Row> rows = new ArrayList<>();
    private final List<String> docs = new ArrayList<>();

    public Step(StepContainer stepContainer, int lineNumber, String keyword, String description) {
        this.stepContainer = stepContainer;
        this.lineNumber = lineNumber;
        this.keyword = keyword;
        this.description = description;
    }

    @Override
    public String getUri() {
        return stepContainer.getUri();
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getComments() {
        return comments;
    }

    public List<Row> getRows() {
        return rows;
    }

    public List<String> getDocs() {
        return docs;
    }

    @Override
    public String toString() {
        return "Step@" + lineNumber + "[" + description + "]";
    }
}
