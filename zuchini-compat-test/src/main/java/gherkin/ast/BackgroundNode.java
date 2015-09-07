package gherkin.ast;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Collections;
import java.util.List;

public class BackgroundNode extends Node {
    private final String keyword;
    private final String name;
    private final String description;
    private final List<StepNode> steps;

    @JsonCreator
    public BackgroundNode(Location location, String keyword, String name, String description, List<StepNode> steps) {
        super(location);
        this.keyword = keyword;
        this.name = name;
        this.description = description;
        this.steps = Collections.unmodifiableList(steps);
    }

    public String getKeyword() {
        return keyword;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<StepNode> getSteps() {
        return steps;
    }

}
