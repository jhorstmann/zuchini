package org.zuchini.examples.datatables;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.zuchini.runner.tables.Datatable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatatableMatcher extends TypeSafeDiagnosingMatcher<Datatable> {
    private final Datatable expected;

    public DatatableMatcher(Datatable expected) {
        super(Datatable.class);
        this.expected = expected;
    }

    public static DatatableMatcher matchesTable(Datatable expected) {
        return new DatatableMatcher(expected);
    }

    private String[][] toArray(Datatable table) {
        List<List<String>> rows = table.getRows();
        String[][] array = new String[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            array[i] = row.toArray(new String[row.size()]);
        }
        return array;
    }

    private String[] formatDatatable(Datatable table) {
        String[][] array = toArray(table);
        int rows = array.length;
        int cols = array[0].length;
        String[] lines = new String[rows];
        int[] widths = new int[cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int len = array[row][col].length();
                if (len > widths[col]) {
                    widths[col] = len;
                }
            }
        }
        for (int row = 0; row < rows; row++) {
            StringBuilder sb = new StringBuilder(128);
            sb.append(" | ");
            for (int col = 0; col < cols; col++) {
                String str = array[row][col];
                sb.append(str);
                for (int i = str.length(); i < widths[col]; i++) {
                    sb.append(" ");
                }
                if (col < cols - 1) {
                    sb.append(" | ");
                }
            }
            sb.append(" |");
            lines[row] = sb.toString();
        }
        return lines;
    }

    @Override
    protected boolean matchesSafely(Datatable item, Description mismatchDescription) {
        String[] expected = formatDatatable(this.expected);
        String[] actual = formatDatatable(item);

        if (Arrays.equals(expected, actual)) {
            return true;
        } else {
            mismatchDescription.appendText("datatable was\n\n");
            for (String line : actual) {
                mismatchDescription.appendText(line).appendText("\n");
            }

            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("datatable matching\n\n");
        for (String line : formatDatatable(expected)) {
            description.appendText(line).appendText("\n");
        }
    }
}
