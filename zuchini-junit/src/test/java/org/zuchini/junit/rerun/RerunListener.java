package org.zuchini.junit.rerun;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.zuchini.junit.description.ScenarioInfo;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class RerunListener extends RunListener {
    private String outputFile;

    @Override
    public void testRunStarted(Description description) throws Exception {
        RerunOptions options = description.getClass().getAnnotation(RerunOptions.class);
        this.outputFile = options != null ? options.outputFile() : RerunOptions.DEFAULT_OUTPUT;
    }

    @Override
    public void testStarted(Description description) throws Exception {
        super.testStarted(description);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void testFinished(Description description) throws Exception {
        super.testFinished(description);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        List<Failure> failures = result.getFailures();
        File file = new File(outputFile);
        file.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(outputFile, "utf-8")) {
            for (Failure failure : failures) {
                Description description = failure.getDescription();
                ScenarioInfo scenarioInfo = description.getAnnotation(ScenarioInfo.class);
                if (scenarioInfo != null) {
                    pw.printf("%s:%d%n", scenarioInfo.uri(), scenarioInfo.lineNumber());
                }
            }
        }
    }
}
