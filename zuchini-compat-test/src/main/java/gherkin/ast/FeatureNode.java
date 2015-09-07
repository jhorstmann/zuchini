package gherkin.ast;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Collections;
import java.util.List;

public class FeatureNode extends Node {
    private final List<TagNode> tags;
    private final String language;
    private final String keyword;
    private final String name;
    private final String description;
    private final BackgroundNode background;
    private final List<ScenarioDefinitionNode> scenarioDefinitions;
    private final List<CommentNode> comments;

    @JsonCreator
    public FeatureNode(
            List<TagNode> tags,
            Location location,
            String language,
            String keyword,
            String name,
            String description,
            BackgroundNode background,
            List<ScenarioDefinitionNode> scenarioDefinitions,
            List<CommentNode> comments) {
        super(location);
        this.tags = Collections.unmodifiableList(tags);
        this.language = language;
        this.keyword = keyword;
        this.name = name;
        this.description = description;
        this.background = background;
        this.scenarioDefinitions = Collections.unmodifiableList(scenarioDefinitions);
        this.comments = Collections.unmodifiableList(comments);
    }

    public List<ScenarioDefinitionNode> getScenarioDefinitions() {
        return scenarioDefinitions;
    }

    public BackgroundNode getBackground() {
        return background;
    }

    public String getLanguage() {
        return language;
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

    public List<TagNode> getTags() {
        return tags;
    }

    public List<CommentNode> getComments() {
        return comments;
    }
}
