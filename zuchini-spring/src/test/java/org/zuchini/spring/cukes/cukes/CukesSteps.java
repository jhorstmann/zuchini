package org.zuchini.spring.cukes.cukes;

import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.springframework.stereotype.Component;
import org.zuchini.annotations.Given;
import org.zuchini.annotations.Then;
import org.zuchini.spring.ScenarioScoped;

@Component
@ScenarioScoped
public class CukesSteps {
    private int cukes;

    @Given("^I have (-?\\d+) cukes in my belly$")
    public void i_have_cukes_in_my_belly(int cukes) {
        if (cukes < 0) {
            throw new IllegalArgumentException("cukes can not be negative");
        } else if(cukes == 0) {
            throw new AssumptionViolatedException("cukes can not be 0");
        }
        this.cukes = cukes;

    }

    @Then("^there are (-?\\d+) cukes in my belly$")
    public void there_are_cukes_in_my_belly(int cukes) {
        Assert.assertEquals(cukes, this.cukes);
    }

}
