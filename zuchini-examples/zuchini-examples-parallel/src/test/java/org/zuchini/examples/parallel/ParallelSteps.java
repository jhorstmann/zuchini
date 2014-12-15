package org.zuchini.examples.parallel;

import org.junit.Assert;
import org.zuchini.annotations.Given;
import org.zuchini.annotations.Then;
import org.zuchini.annotations.When;

public class ParallelSteps {
    private int counter;

    @Given("^the counter is initialized to (\\d+)$")
    public void the_counter_is(int initialValue) {
        this.counter = initialValue;
    }

    @When("^the counter is incremented$")
    public void the_counter_is_incremented() throws Throwable {
        int newValue = counter + 1;

        Thread.sleep(1000);

        this.counter = newValue;
    }

    @Then("^the counter should be (\\d+)$")
    public void the_counter_should_be(int expectedValue) throws Throwable {
        Assert.assertEquals(expectedValue, counter);
    }

}
