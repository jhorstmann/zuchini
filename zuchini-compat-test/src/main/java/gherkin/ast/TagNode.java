package gherkin.ast;

import com.fasterxml.jackson.annotation.JsonCreator;

public class TagNode extends Node {
    private final String name;

    @JsonCreator
    public TagNode(Location location, String name) {
        super(location);
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
