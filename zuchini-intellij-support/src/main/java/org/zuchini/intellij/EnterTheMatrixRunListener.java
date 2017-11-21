package org.zuchini.intellij;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.zuchini.junit.description.FeatureInfo;
import org.zuchini.junit.description.OutlineInfo;
import org.zuchini.junit.description.ScenarioInfo;
import org.zuchini.junit.description.StepInfo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

class EnterTheMatrixRunListener extends RunListener {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ");
    private static final String TEMPLATE_TEST_STARTED =
            "##teamcity[testStarted timestamp = '%s' locationHint = 'file:///%s' captureStandardOutput = 'true' name = '%s']";
    private static final String TEMPLATE_TEST_FAILED =
            "##teamcity[testFailed timestamp = '%s' details = '%s' message = '%s' name = '%s' %s]";
    private static final String TEMPLATE_TEST_PENDING =
            "##teamcity[testIgnored timestamp = '%s' name = '%s' message = 'Skipped step']";
    private static final String TEMPLATE_TEST_FINISHED =
            "##teamcity[testFinished timestamp = '%s' duration = '0' name = '%s']";
    private static final String TEMPLATE_ENTER_THE_MATRIX = "##teamcity[enteredTheMatrix timestamp = '%s']";
    private static final String TEMPLATE_TEST_SUITE_STARTED =
            "##teamcity[testSuiteStarted timestamp = '%s' locationHint = 'file://%s' name = '%s']";
    private static final String TEMPLATE_TEST_SUITE_FINISHED = "##teamcity[testSuiteFinished timestamp = '%s' name = '%s']";

    private final Appendable out;
    private FeatureInfo currentFeature;
    private ScenarioInfo currentScenario;

    EnterTheMatrixRunListener(Appendable out) {
        this.out = out;
    }

    private static synchronized String getCurrentTime() {
        return DATE_FORMAT.format(new Date());
    }

    private static String escape(String source) {
        return source.replace("|", "||")
                     .replace("\n", "|n")
                     .replace("\r", "|r")
                     .replace("'", "|'")
                     .replace("[", "|[")
                     .replace("]", "|]");
    }

    private void printf(String format, Object... args) {
        try {
            out.append('\n');
            out.append(String.format(Locale.ROOT, format, args));
            out.append('\n');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        printf(TEMPLATE_ENTER_THE_MATRIX, getCurrentTime());
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        printf(TEMPLATE_TEST_SUITE_FINISHED, getCurrentTime(), currentScenario.name());
        printf(TEMPLATE_TEST_SUITE_FINISHED, getCurrentTime(), currentFeature.name());
    }

    @Override
    public void testStarted(Description description) throws Exception {
        FeatureInfo feature = description.getAnnotation(FeatureInfo.class);
        ScenarioInfo scenario = description.getAnnotation(ScenarioInfo.class);
        OutlineInfo outline = description.getAnnotation(OutlineInfo.class);
        StepInfo step = description.getAnnotation(StepInfo.class);
        String timestamp = getCurrentTime();

        if (currentFeature == null) {
            printf(TEMPLATE_TEST_SUITE_STARTED, timestamp, feature.uri() + ":" + feature.lineNumber(), feature.name());
            if (step != null) {
                printf(TEMPLATE_TEST_SUITE_STARTED, timestamp, scenario.uri() + ":" + scenario.lineNumber(), scenario.name());
            } else {
                printf(TEMPLATE_TEST_STARTED, timestamp, scenario.uri() + ":" + scenario.lineNumber(), scenario.name());
            }
        }

        if (currentFeature != null && !Objects.equals(feature, currentFeature)) {
            printf(TEMPLATE_TEST_SUITE_FINISHED, timestamp, currentScenario.name());
            printf(TEMPLATE_TEST_SUITE_FINISHED, timestamp, currentFeature.name());

            printf(TEMPLATE_TEST_SUITE_STARTED, timestamp, feature.uri() + ":" + feature.lineNumber(), feature.name());
            if (step != null) {
                printf(TEMPLATE_TEST_SUITE_STARTED, timestamp, scenario.uri() + ":" + scenario.lineNumber(), scenario.name());
            } else {
                printf(TEMPLATE_TEST_STARTED, timestamp, scenario.uri() + ":" + scenario.lineNumber(), scenario.name());
            }
        } else if (currentScenario != null && !Objects.equals(scenario, currentScenario)) {
            printf(TEMPLATE_TEST_SUITE_FINISHED, timestamp, currentScenario.name());

            if (step != null) {
                printf(TEMPLATE_TEST_SUITE_STARTED, timestamp, scenario.uri() + ":" + scenario.lineNumber(), scenario.name());
            } else {
                printf(TEMPLATE_TEST_STARTED, timestamp, scenario.uri() + ":" + scenario.lineNumber(), scenario.name());
            }
        }

        if (step != null) {
            printf(TEMPLATE_TEST_STARTED, timestamp, step.uri() + ":" + step.lineNumber(), step.name());
        }

        currentFeature = feature;
        currentScenario = scenario;
    }

    private static String getTestName(Description description) {
        ScenarioInfo scenario = description.getAnnotation(ScenarioInfo.class);
        StepInfo step = description.getAnnotation(StepInfo.class);
        return step != null ? step.name() : scenario.name();
    }

    @Override
    public void testFinished(Description description) throws Exception {
        String name = getTestName(description);
        String timestamp = getCurrentTime();
        printf(TEMPLATE_TEST_FINISHED, timestamp, name);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        Description description = failure.getDescription();
        String name = getTestName(description);
        String timestamp = getCurrentTime();
        failure.getException().printStackTrace();
        printf(TEMPLATE_TEST_FAILED, timestamp, "", escape(failure.getMessage()), name, "error = 'true'");
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        Description description = failure.getDescription();
        String name = getTestName(description);
        String timestamp = getCurrentTime();
        failure.getException().printStackTrace();
        printf(TEMPLATE_TEST_PENDING, timestamp, name);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        String name = getTestName(description);
        String timestamp = getCurrentTime();
        printf(TEMPLATE_TEST_PENDING, timestamp, name);
        printf(TEMPLATE_TEST_FINISHED, timestamp, name);
    }
}
