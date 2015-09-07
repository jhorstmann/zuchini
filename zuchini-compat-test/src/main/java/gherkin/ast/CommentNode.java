package gherkin.ast;

import com.fasterxml.jackson.annotation.JsonCreator;

public class CommentNode extends Node {
    private final String text;

    @JsonCreator
    public CommentNode(Location location, String text) {
        super(location);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
