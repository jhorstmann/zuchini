package org.zuchini.gherkin.model.builder;

import org.zuchini.gherkin.model.Row;
import org.zuchini.gherkin.model.Step;
import org.zuchini.gherkin.model.StepContainer;

import java.util.ArrayList;
import java.util.List;

public class StepBuilder {
    private StepContainer stepContainer;
    private int lineNumber;
    private String description;
    private String keyword;
    private List<String> comments = new ArrayList<>();
    private List<String> docs = new ArrayList<>();
    private List<Row> rows = new ArrayList<>();

    public StepBuilder(StepContainer stepContainer) {
        this.stepContainer = stepContainer;

    }

    public StepBuilder withLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }

    public StepBuilder withKeyword(String keyword) {
        this.keyword = keyword;
        return this;
    }

    public StepBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public StepBuilder withComment(String comment) {
        this.comments.add(comment);
        return this;
    }

    public StepBuilder withRow(Row row) {
        this.rows.add(row);
        return this;
    }

    public StepBuilder withDocs(String doc) {
        this.docs.add(doc);
        return this;
    }

    public Step build() {
        Step step = new Step(stepContainer, lineNumber, keyword, description);
        step.getComments().addAll(comments);
        step.getRows().addAll(rows);

        return step;
    }


}
