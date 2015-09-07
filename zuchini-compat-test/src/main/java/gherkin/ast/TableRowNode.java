package gherkin.ast;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Collections;
import java.util.List;

public class TableRowNode extends Node {
    private final List<TableCellNode> cells;

    @JsonCreator
    public TableRowNode(Location location, List<TableCellNode> cells) {
        super(location);
        this.cells = Collections.unmodifiableList(cells);
    }

    public List<TableCellNode> getCells() {
        return cells;
    }

}
