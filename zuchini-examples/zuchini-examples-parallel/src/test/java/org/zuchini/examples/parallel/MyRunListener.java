package org.zuchini.examples.parallel;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import java.util.Date;

public class MyRunListener extends RunListener {
    @Override
    public void testRunStarted(Description description) throws Exception {
        System.out.println(new Date() + " : run started");
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        System.out.println(new Date() + " : run finished");
    }

    @Override
    public void testStarted(Description description) throws Exception {
        System.out.println(new Date() + " : started " + description.getDisplayName());
    }

    @Override
    public void testFinished(Description description) throws Exception {
        System.out.println(new Date() + " : finished " + description.getDisplayName());
    }
}
