package org.zuchini.examples.filtering;

import org.zuchini.annotations.Given;

import java.util.concurrent.atomic.AtomicInteger;

public class FilteringSteps {

    @Given("^a successful step$")
    public void successful() {
    }

    @Given("^a failing step$")
    public void failure() {
        throw new RuntimeException("step failed");
    }

}
