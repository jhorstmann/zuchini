package org.zuchini.gherkin.model;

import java.util.ArrayList;
import java.util.List;

public class Examples implements Tagged, Commented, LocationAware, RowContainer {
    private final Outline outline;
    private final int lineNumber;
    private final String description;
    private final List<String> comments = new ArrayList<>();
    private final List<String> tags = new ArrayList<>();
    private final List<Row> rows = new ArrayList<>();

    public Examples(Outline outline, int lineNumber, String description) {
        this.outline = outline;
        this.lineNumber = lineNumber;
        this.description = description;
    }

    @Override
    public String getUri() {
        return outline.getUri();
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getComments() {
        return comments;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<Row> getRows() {
        return rows;
    }
}
