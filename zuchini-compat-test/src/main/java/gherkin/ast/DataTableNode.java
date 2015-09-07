package gherkin.ast;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Collections;
import java.util.List;

public class DataTableNode extends ArgumentNode {
    private final List<TableRowNode> rows;

    @JsonCreator
    public DataTableNode(List<TableRowNode> rows) {
        super(rows.get(0).getLocation());
        this.rows = Collections.unmodifiableList(rows);
    }

    public List<TableRowNode> getRows() {
        return rows;
    }
}
