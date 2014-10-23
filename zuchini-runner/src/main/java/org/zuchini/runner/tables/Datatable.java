package org.zuchini.runner.tables;

import org.zuchini.model.Row;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Datatable {
    private final List<List<String>> rows;

    public Datatable(List<Row> rows) {
        this.rows = new ArrayList<>(rows.size());
        for (Row row : rows) {
            this.rows.add(row.getCells());
        }
    }

    public Datatable(List<Map<String, String>> objects, List<String> header, NamingConvention namingConvention) {
        List<List<String>> rows = new ArrayList<>(objects.size() + 1);

        rows.add(header);

        for (Map<String, String> object : objects) {
            List<String> row = new ArrayList<>(header.size());
            for (String title : header) {
                row.add(object.get(namingConvention.toProperty(title)));
            }
            rows.add(row);
        }
        this.rows = rows;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public List<String> getHeader() {
        return rows.get(0);
    }

    public List<List<String>> getData() {
        return rows.subList(1, rows.size());
    }

    public List<Map<String, String>> toMap(NamingConvention namingConvention) {
        List<String> headerCells = getHeader();
        String[] headerProperties = new String[headerCells.size()];
        for (int i = 0, len = headerCells.size(); i < len; i++) {
            headerProperties[i] = headerCells.get(i);
        }
        List<List<String>> rows = getData();
        List<Map<String, String>> objects = new ArrayList<>(rows.size());
        for (List<String> row : rows) {
            Map<String, String> map = new LinkedHashMap<>(headerCells.size());

            for (int i = 0, len = row.size(); i < len; i++) {
                map.put(headerProperties[i], row.get(i));
            }
            objects.add(map);
        }
        return objects;
    }
}
