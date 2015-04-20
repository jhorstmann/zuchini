package org.zuchini.examples.datatables;

import org.zuchini.annotations.Given;
import org.zuchini.annotations.Then;
import org.zuchini.runner.tables.Datatable;
import org.zuchini.runner.tables.NamingConvention;
import org.zuchini.runner.tables.NamingConventions.DefaultNamingConventions;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.zuchini.runner.tables.DatatableMatchers.matchesTable;

public class DatatableSteps {
    private NamingConvention namingConvention;
    private List<Map<String, String>> table;

    private static String toConstantName(String name) {
        return name.toUpperCase(Locale.ROOT).replace(' ', '_');
    }

    @Given("a datatable with \"([^\"]+)\" naming convention")
    public void datatable_identity(String name, Datatable table) {
        this.namingConvention = Enum.valueOf(DefaultNamingConventions.class, toConstantName(name));
        this.table = table.toMap(namingConvention);
    }

    @Then("the column \"([^\"]+)\" in row \"([0-9]+)\" is \"([^\"]*)\"")
    public void the_column_value_is(String column, int row, String value) {
        final Map<String, String> map = table.get(row - 1);

        assertEquals(value, map.get(column));
    }

    @Then("the datatable matches")
    public void the_datatable_matches(Datatable expected) {
        final Datatable actual = Datatable.fromMaps(this.table, expected.getHeader(), namingConvention);

        assertThat(actual, matchesTable(expected));
    }
}
