package gherkin.ast;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

public class ScenarioNode extends ScenarioDefinitionNode {
    @JsonCreator
    public ScenarioNode(List<TagNode> tags, Location location, String keyword, String name, String description, List<StepNode> steps) {
        super(tags, location, keyword, name, description, steps);
    }

}
