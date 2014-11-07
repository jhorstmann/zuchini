package org.zuchini.cucumber.cukes;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class DebugListener extends RunListener {

    @Override
    public void testRunStarted(Description description) throws Exception {
        System.out.println("testRunStarted " + description.getDisplayName());
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        System.out.println("testRunFinished ");
    }

    @Override
    public void testStarted(Description description) throws Exception {
        System.out.println("testStarted " + description.getDisplayName());
    }

    @Override
    public void testFinished(Description description) throws Exception {
        System.out.println("testFinished " + description.getDisplayName());
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        System.out.println("testFailure" + failure.getMessage() + " " + failure.getDescription().getDisplayName());
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        System.out.println("testAssumptionFailure" + failure.getMessage() + " " + failure.getDescription().getDisplayName());
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        System.out.println("testIgnored " + description.getDisplayName());
    }
}
