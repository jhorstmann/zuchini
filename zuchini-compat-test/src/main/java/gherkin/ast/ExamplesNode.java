package gherkin.ast;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Collections;
import java.util.List;

public class ExamplesNode extends Node {
    private final List<TagNode> tags;
    private final String keyword;
    private final String name;
    private final String description;
    private final TableRowNode tableHeader;
    private final List<TableRowNode> tableBody;

    @JsonCreator
    public ExamplesNode(Location location, List<TagNode> tags, String keyword, String name, String description, TableRowNode tableHeader, List<TableRowNode> tableBody) {
        super(location);
        this.tags = Collections.unmodifiableList(tags);
        this.keyword = keyword;
        this.name = name;
        this.description = description;
        this.tableHeader = tableHeader;
        this.tableBody = Collections.unmodifiableList(tableBody);
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

    public List<TableRowNode> getTableBody() {
        return tableBody;
    }

    public TableRowNode getTableHeader() {
        return tableHeader;
    }

    public List<TagNode> getTags() {
        return tags;
    }
}
