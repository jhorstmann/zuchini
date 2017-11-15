package org.zuchini.junit5.helloworld;

import org.zuchini.annotations.Given;
import org.zuchini.annotations.Then;
import org.zuchini.annotations.When;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldSteps {
    private String name;
    private String output;

    @Given("^the user name is '([^']+)'$")
    public void userNameIs(String name) {
        this.name = name;
    }

    @When("^the user clicks the hello button$")
    public void clickTheButton() {
        this.output = "Hello " + name;
    }

    @Then("^the output is '([^']+)'")
    public void outputIs(String expectedOutput) {
        assertEquals(expectedOutput, output);
    }


}
