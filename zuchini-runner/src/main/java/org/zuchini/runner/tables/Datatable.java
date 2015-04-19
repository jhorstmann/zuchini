package org.zuchini.runner.tables;

import org.zuchini.model.Row;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Datatable {
    private final List<List<String>> rows;

    private Datatable(List<List<String>> rows) {
        this.rows = rows;
    }

    public static Datatable fromRows(List<Row> rows) {
        List<List<String>> list = new ArrayList<>(rows.size());
        for (Row row : rows) {
            list.add(row.getCells());
        }
        return new Datatable(list);
    }

    public static Datatable fromLists(List<List<String>> lists) {
        return new Datatable(lists);
    }

    public static Datatable fromMaps(List<Map<String, String>> objects, List<String> header, NamingConvention namingConvention) {
        List<List<String>> rows = new ArrayList<>(objects.size() + 1);

        rows.add(header);

        for (Map<String, String> object : objects) {
            List<String> row = new ArrayList<>(header.size());
            for (String title : header) {
                String value = object.get(namingConvention.toProperty(title));
                row.add(value == null ? "" : value);
            }
            rows.add(row);
        }

        return new Datatable(rows);
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
            headerProperties[i] = namingConvention.toProperty(headerCells.get(i));
        }
        List<List<String>> rows = getData();
        List<Map<String, String>> objects = new ArrayList<>(rows.size());
        for (List<String> row : rows) {
            assert(row.size() == headerProperties.length);
            Map<String, String> map = new LinkedHashMap<>(headerProperties.length);

            for (int i = 0, len = row.size(); i < len; i++) {
                map.put(headerProperties[i], row.get(i));
            }
            objects.add(map);
        }
        return objects;
    }
}
