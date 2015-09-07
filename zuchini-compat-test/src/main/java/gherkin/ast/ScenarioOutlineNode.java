package gherkin.ast;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Collections;
import java.util.List;

public class ScenarioOutlineNode extends ScenarioDefinitionNode {
    private final List<ExamplesNode> examples;

    @JsonCreator
    public ScenarioOutlineNode(List<TagNode> tags, Location location, String keyword, String name, String description, List<StepNode> steps, List<ExamplesNode> examples) {
        super(tags, location, keyword, name, description, steps);
        this.examples = Collections.unmodifiableList(examples);
    }

    public List<ExamplesNode> getExamples() {
        return examples;
    }
}
