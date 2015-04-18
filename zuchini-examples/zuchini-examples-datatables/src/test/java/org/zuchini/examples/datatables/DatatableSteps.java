package org.zuchini.examples.datatables;

import org.junit.Assert;
import org.zuchini.annotations.Given;
import org.zuchini.annotations.Then;
import org.zuchini.runner.tables.Datatable;
import org.zuchini.runner.tables.NamingConventions;

import java.util.List;
import java.util.Map;

public class DatatableSteps {
    private List<Map<String, String>> table;

    @Given("a datatable with identity naming convention")
    public void datatable_identity(Datatable table) {
        this.table = table.toMap(NamingConventions.DefaultNamingConventions.IDENTITY);
    }

    @Given("a datatable with lowercase naming convention")
    public void datatable_lowercase(Datatable table) {
        this.table = table.toMap(NamingConventions.DefaultNamingConventions.LOWERCASE_WORDS);
    }

    @Given("a datatable with uppercase naming convention")
    public void datatable_uppercase(Datatable table) {
        this.table = table.toMap(NamingConventions.DefaultNamingConventions.UPPERCASE_WORDS);
    }

    @Then("the column \"([^\"]+)\" in row \"([0-9]+)\" is \"([^\"]*)\"")
    public void the_column_value_is(String column, int row, String value) {
        final Map<String, String> map = table.get(row - 1);

        Assert.assertEquals(value, map.get(column));
    }
}
