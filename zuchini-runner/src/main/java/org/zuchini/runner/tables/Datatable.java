package org.zuchini.runner.tables;

import org.zuchini.model.Row;

import java.util.List;

public class Datatable {
    private final List<Row> rows;

    public Datatable(List<Row> rows) {
        this.rows = rows;
    }

    public List<Row> getRows() {
        return rows;
    }

    public Row getHeader() {
        return rows.get(0);
    }

    public List<Row> getData() {
        return rows.subList(1, rows.size());
    }
}
