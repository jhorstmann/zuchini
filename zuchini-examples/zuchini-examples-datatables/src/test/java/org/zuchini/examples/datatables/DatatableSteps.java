package org.zuchini.examples.datatables;

import org.junit.Assert;
import org.zuchini.annotations.Given;
import org.zuchini.annotations.Then;
import org.zuchini.runner.tables.Datatable;
import org.zuchini.runner.tables.NamingConvention;
import org.zuchini.runner.tables.NamingConventions;

import java.util.List;
import java.util.Map;

public class DatatableSteps {
    private List<Map<String, String>> table;

    @Given("a datatable")
    public void datatable(Datatable table) {
        this.table = table.toMap(NamingConventions.DefaultNamingConventions.IDENTITY);
    }

    @Then("the column \"([^\"]+)\" in row \"([0-9]+)\" is \"([^\"]*)\"")
    public void the_column_value_is(String column, int row, String value) {
        final Map<String, String> map = table.get(row - 1);

        Assert.assertEquals(value, map.get(column));
    }
}
