package org.zuchini.junit.cukes;

import org.junit.Assert;
import org.zuchini.annotations.Given;
import org.zuchini.annotations.Then;

public class CukesSteps {
    private int cukes;

    @Given("^I have (\\d+) cukes in my belly$")
    public void i_have_cukes_in_my_belly(int cukes) {
        this.cukes = cukes;

    }

    @Then("^there are (\\d+) cukes in my belly$")
    public void there_are_cukes_in_my_belly(int cukes) {
        Assert.assertEquals(cukes, this.cukes);
    }

}
