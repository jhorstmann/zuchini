package org.zuchini.runner.tables;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class DatatableTest {

    private static Map<String, String> example(int width, int height, String longDescription) {
        Map<String, String> map = new HashMap<>();
        map.put("width", String.valueOf(width));
        map.put("height", String.valueOf(height));
        map.put("longDescription", longDescription);
        return map;
    }

    @Test
    public void tableShouldGetMappedWithNamingConvention() {
        List<String> header = asList("Width", "Height", "Long Description");
        Map<String, String> row1 = example(640, 480, "Example 1");
        Map<String, String> row2 = example(800, 600, "Example 2");
        Datatable datatable = new Datatable(asList(row1, row2),
                header, NamingConventions.DefaultNamingConventions.TITLECASE);

        assertEquals(header, datatable.getHeader());
        assertEquals(3, datatable.getRows().size());
        assertEquals(asList("Width", "Height", "Long Description"), datatable.getRows().get(0));
        assertEquals(asList("640", "480", "Example 1"), datatable.getRows().get(1));
        assertEquals(asList("800", "600", "Example 2"), datatable.getRows().get(2));

        assertEquals(2, datatable.getData().size());
        assertEquals(asList("640", "480", "Example 1"), datatable.getData().get(0));
        assertEquals(asList("800", "600", "Example 2"), datatable.getData().get(1));
    }
}
