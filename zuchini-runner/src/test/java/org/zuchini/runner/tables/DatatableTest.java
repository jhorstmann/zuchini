package org.zuchini.runner.tables;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class DatatableTest {

    private static Map<String, String> example(int width, int height, String longDescription) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("width", String.valueOf(width));
        map.put("height", String.valueOf(height));
        map.put("longDescription", longDescription);
        return map;
    }

    @Test
    public void tableCanBeConstructedFromMaps() {
        List<String> header = asList("Width", "Height", "Long Description");
        Map<String, String> row1 = example(640, 480, "Example 1");
        Map<String, String> row2 = example(800, 600, "Example 2");
        Datatable datatable = Datatable.fromMaps(asList(row1, row2),
                header, NamingConventions.DefaultNamingConventions.UPPERCASE_WORDS);

        assertEquals(header, datatable.getHeader());
        assertEquals(3, datatable.getRows().size());
        assertEquals(asList("Width", "Height", "Long Description"), datatable.getRows().get(0));
        assertEquals(asList("640", "480", "Example 1"), datatable.getRows().get(1));
        assertEquals(asList("800", "600", "Example 2"), datatable.getRows().get(2));

        assertEquals(2, datatable.getData().size());
        assertEquals(asList("640", "480", "Example 1"), datatable.getData().get(0));
        assertEquals(asList("800", "600", "Example 2"), datatable.getData().get(1));
    }

    @Test
    public void tableCanBeConvertedToMaps() {
        List<String> header = asList("Width", "Height", "Long Description");
        List<String> row1 = asList("640", "480", "Example 1");
        List<String> row2 = asList("800", "600", "Example 2");
        Datatable datatable = Datatable.fromLists(asList(header, row1, row2));

        List<Map<String, String>> maps = datatable.toMap(NamingConventions.DefaultNamingConventions.UPPERCASE_WORDS);
        assertEquals(2, maps.size());
        assertEquals(example(640, 480, "Example 1"), maps.get(0));
        assertEquals(example(800, 600, "Example 2"), maps.get(1));
    }

}
