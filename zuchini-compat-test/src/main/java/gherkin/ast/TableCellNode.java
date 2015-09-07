package gherkin.ast;

import com.fasterxml.jackson.annotation.JsonCreator;

public class TableCellNode extends Node {
    private final String value;

    @JsonCreator
    public TableCellNode(Location location, String value) {
        super(location);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
