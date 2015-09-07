package gherkin.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class StepNode extends Node {
    private final String keyword;
    private final String text;
    private final ArgumentNode argument;

    @JsonCreator
    public StepNode(Location location, @JsonDeserialize(converter=TrimConverter.class) String keyword, String text, ArgumentNode argument) {
        super(location);
        this.keyword = keyword;
        this.text = text;
        this.argument = argument;
    }

    public String getText() {
        return text;
    }

    public String getKeyword() {
        return keyword;
    }

    public ArgumentNode getArgument() {
        return argument;
    }

}
