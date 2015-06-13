package org.zuchini.examples.datatables;

import org.junit.Assume;
import org.zuchini.annotations.Given;
import org.zuchini.annotations.Then;
import org.zuchini.annotations.When;
import org.zuchini.runner.tables.Datatable;

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

    @Given("^a failing assumption$")
    public void a_failing_assumption() {
        Assume.assumeTrue("a failing assumption", false);
    }

}
