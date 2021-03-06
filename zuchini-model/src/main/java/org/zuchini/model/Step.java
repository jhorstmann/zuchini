package org.zuchini.model;

import java.util.ArrayList;
import java.util.List;

public class Step implements Named, Commented, Tagged, LocationAware, RowContainer {
    private final StepContainer stepContainer;
    private final int lineNumber;
    private final String keyword;
    private final String name;
    private final List<String> tags = new ArrayList<>();
    private final List<String> comments = new ArrayList<>();
    private final List<Row> rows = new ArrayList<>();
    private final List<String> docs = new ArrayList<>();

    public Step(StepContainer stepContainer, int lineNumber, String keyword, String name) {
        this.stepContainer = stepContainer;
        this.lineNumber = lineNumber;
        this.keyword = keyword;
        this.name = name;
    }

    public StepContainer getStepContainer() {
        return stepContainer;
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

    public String getName() {
        return name;
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
        return "Step@" + lineNumber + "[" + name + "]";
    }
}
