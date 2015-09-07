package gherkin.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Location {
    private final int line;
    private final int column;

    @JsonCreator
    public Location(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    // TODO: not supported yet
    @JsonIgnore
    public int getColumn() {
        return column;
    }
}
