package org.zuchini.examples.flaky;

import org.zuchini.annotations.Given;

import java.util.concurrent.atomic.AtomicInteger;

public class FlakySteps {
    private static final AtomicInteger counter1 = new AtomicInteger();
    private static final AtomicInteger counter2 = new AtomicInteger();

    @Given("^a step that succeeds on the second try$")
    public void succeedOnSecondTry() throws Throwable {
        int i = counter1.getAndIncrement();
        if (i < 1) {
            throw new RuntimeException("Step failed on try " + i);
        }
    }

    @Given("^a step that succeeds on the third try$")
    public void succeedOnThirdTry() throws Throwable {
        int i = counter2.getAndIncrement();
        if (i < 2) {
            throw new RuntimeException("Step failed on try " + i);
        }
    }

}
