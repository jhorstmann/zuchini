package org.zuchini.examples.datatables;

import org.zuchini.annotations.Given;
import org.zuchini.annotations.Then;
import org.zuchini.annotations.When;
import org.zuchini.runner.tables.Datatable;
import org.zuchini.runner.tables.NamingConvention;
import org.zuchini.runner.tables.NamingConventions.DefaultNamingConventions;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.zuchini.runner.tables.DatatableMatchers.matchesTable;

public class ReportingSteps {
    @Given("^a simple scenario$")
    public void a_simple_scenario() {

    }

    @When("^the scenario is executed$")
    public void the_scenario_is_executed() {

    }

    @Then("^a report is generated$")
    public void a_report_is_generated() {

    }

    @Given("^a datatable$")
    public void a_datatable(Datatable table) {

    }

}
