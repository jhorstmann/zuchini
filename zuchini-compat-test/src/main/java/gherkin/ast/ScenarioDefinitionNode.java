package gherkin.ast;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Collections;
import java.util.List;

@JsonSubTypes({ @JsonSubTypes.Type(value = ScenarioNode.class , name = "Scenario"), @JsonSubTypes.Type(value = ScenarioOutlineNode.class, name = "ScenarioOutline")})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,  property = "type")
public abstract class ScenarioDefinitionNode extends Node {
    private final List<TagNode> tags;
    private final String keyword;
    private final String name;
    private final String description;
    private final List<StepNode> steps;

    public ScenarioDefinitionNode(List<TagNode> tags, Location location, String keyword, String name, String description, List<StepNode> steps) {
        super(location);
        this.tags = Collections.unmodifiableList(tags);
        this.keyword = keyword;
        this.name = name;
        this.description = description;
        this.steps = Collections.unmodifiableList(steps);
    }

    public String getName() {
        return name;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getDescription() {
        return description;
    }

    public List<StepNode> getSteps() {
        return steps;
    }

    public List<TagNode> getTags() {
        return tags;
    }
}
