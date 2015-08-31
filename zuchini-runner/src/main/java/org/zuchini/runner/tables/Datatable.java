package org.zuchini.runner.tables;

import org.zuchini.model.Row;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Datatable {
    private final List<List<String>> rows;

    private Datatable(final List<List<String>> rows) {
        this.rows = rows;
    }

    private static List<String> unmodifiableCopy(final List<String> list) {
        final String[] array = list.toArray(new String[list.size()]);
        return Collections.unmodifiableList(Arrays.asList(array));
    }

    public static Datatable fromRows(final List<Row> rows) {
        final List<List<String>> list = new ArrayList<>(rows.size());
        for (Row row : rows) {
            final List<String> cells = row.getCells();
            list.add(unmodifiableCopy(cells));
        }
        return new Datatable(Collections.unmodifiableList(list));
    }

    public static Datatable fromLists(final List<List<String>> rows) {
        final List<List<String>> list = new ArrayList<>(rows.size());
        for (List<String> row : rows) {
            list.add(unmodifiableCopy(row));
        }
        return new Datatable(Collections.unmodifiableList(list));
    }

    public static Datatable fromMaps(final List<Map<String, String>> objects, final List<String> header, final NamingConvention namingConvention) {
        final List<List<String>> rows = new ArrayList<>(objects.size() + 1);

        rows.add(header);

        for (Map<String, String> object : objects) {
            final List<String> row = new ArrayList<>(header.size());
            for (String title : header) {
                final String value = object.get(namingConvention.toProperty(title));
                row.add(value == null ? "" : value);
            }
            rows.add(row);
        }

        return new Datatable(Collections.unmodifiableList(rows));
    }

    public static Datatable fromMaps(final List<Map<String, String>> objects, final List<String> header) {
        return fromMaps(objects, header, NamingConventions.DefaultNamingConventions.IDENTITY);
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

    public List<Map<String, String>> toMap(final NamingConvention namingConvention) {
        final List<String> headerCells = getHeader();
        final String[] headerProperties = new String[headerCells.size()];
        for (int i = 0, len = headerCells.size(); i < len; i++) {
            headerProperties[i] = namingConvention.toProperty(headerCells.get(i));
        }
        final List<List<String>> rows = getData();
        final List<Map<String, String>> objects = new ArrayList<>(rows.size());
        for (List<String> row : rows) {
            assert(row.size() == headerProperties.length);
            final Map<String, String> map = new LinkedHashMap<>(headerProperties.length);

            for (int i = 0, len = row.size(); i < len; i++) {
                map.put(headerProperties[i], row.get(i));
            }
            objects.add(map);
        }
        return objects;
    }

    public List<Map<String, String>> toMap() {
        return toMap(NamingConventions.DefaultNamingConventions.IDENTITY);
    }
}
