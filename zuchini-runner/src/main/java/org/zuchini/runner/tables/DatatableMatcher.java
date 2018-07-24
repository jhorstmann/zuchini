package org.zuchini.runner.tables;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Arrays;
import java.util.List;

class DatatableMatcher extends TypeSafeDiagnosingMatcher<Datatable> {

    private final Datatable expected;

    DatatableMatcher(Datatable expected) {
        super(Datatable.class);
        this.expected = expected;
    }

    private static String[][] toArray(Datatable table) {
        List<List<String>> rows = table.getRows();
        String[][] array = new String[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            array[i] = row.toArray(new String[row.size()]);
        }
        return array;
    }

    private static void updateWidths(int[] widths, String[][] array) {
        int rows = array.length;
        int cols = array[0].length;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int len = array[row][col].length();
                if (len > widths[col]) {
                    widths[col] = len;
                }
            }
        }
    }

    private static String[] formatDatatable(String[][] array, int[] widths) {
        int rows = array.length;
        int cols = array[0].length;
        String[] lines = new String[rows];
        for (int row = 0; row < rows; row++) {
            assert (array[row].length == widths.length);

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

    private static String[] formatDatatable(Datatable table) {
        String[][] array = toArray(table);
        int[] widths = new int[array[0].length];
        updateWidths(widths, array);
        return formatDatatable(array, widths);
    }

    private static String[] formatDiff(String[] lines1, String[] lines2) {
        return Diff.formatDiff(lines1, lines2);
    }

    @Override
    protected boolean matchesSafely(Datatable item, Description mismatchDescription) {
        String[][] expectedArray = toArray(this.expected);
        String[][] actualArray = toArray(item);

        int[] widths = new int[expectedArray[0].length];
        updateWidths(widths, expectedArray);
        updateWidths(widths, actualArray);

        String[] expectedLines = formatDatatable(expectedArray, widths);
        String[] actualLines = formatDatatable(actualArray, widths);

        if (Arrays.equals(expectedLines, actualLines)) {
            return true;
        } else {
            mismatchDescription.appendText("did not match\n\n");
            for (String line : formatDiff(expectedLines, actualLines)) {
                mismatchDescription.appendText(line).appendText("\n");
            }

            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("datatable matching\n\n");
        for (String line : formatDatatable(this.expected)) {
            description.appendText(" ").appendText(line).appendText("\n");
        }
    }
}
