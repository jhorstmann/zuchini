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
    private static final String TEMPLATE_SCENARIO_FAILED = "##teamcity[customProgressStatus timestamp='%s' type='testFailed']";
    private static final String TEMPLATE_TEST_PENDING =
            "##teamcity[testIgnored name = '%s' message = 'Skipped step' timestamp = '%s']";
    private static final String TEMPLATE_TEST_FINISHED =
            "##teamcity[testFinished timestamp = '%s' diagnosticInfo = 'cucumber f/s=(1344855950447, 1344855950447), duration=0, time.now=%s' duration = '0' name = '%s']";
    private static final String TEMPLATE_ENTER_THE_MATRIX = "##teamcity[enteredTheMatrix timestamp = '%s']";
    private static final String TEMPLATE_TEST_SUITE_STARTED =
            "##teamcity[testSuiteStarted timestamp = '%s' locationHint = 'file://%s' name = '%s']";
    private static final String TEMPLATE_TEST_SUITE_FINISHED = "##teamcity[testSuiteFinished timestamp = '%s' name = '%s']";
    private static final String TEMPLATE_SCENARIO_COUNTING_STARTED =
            "##teamcity[customProgressStatus testsCategory = 'Scenarios' count = '0' timestamp = '%s']";
    private static final String TEMPLATE_SCENARIO_COUNTING_FINISHED =
            "##teamcity[customProgressStatus testsCategory = '' count = '0' timestamp = '%s']";
    private static final String TEMPLATE_SCENARIO_STARTED = "##teamcity[customProgressStatus type = 'testStarted' timestamp = '%s']";
    private final Appendable out;
    private FeatureInfo currentFeature;
    private ScenarioInfo currentScenario;

    public EnterTheMatrixRunListener(Appendable out) {
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
        printf(TEMPLATE_SCENARIO_COUNTING_STARTED, getCurrentTime());
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        printf(TEMPLATE_TEST_SUITE_FINISHED, getCurrentTime(), currentScenario.description());
        printf(TEMPLATE_TEST_SUITE_FINISHED, getCurrentTime(), currentFeature.description());

        printf(TEMPLATE_SCENARIO_COUNTING_FINISHED, getCurrentTime());
    }

    @Override
    public void testStarted(Description description) throws Exception {
        FeatureInfo feature = description.getAnnotation(FeatureInfo.class);
        ScenarioInfo scenario = description.getAnnotation(ScenarioInfo.class);
        OutlineInfo outline = description.getAnnotation(OutlineInfo.class);
        StepInfo step = description.getAnnotation(StepInfo.class);
        String timestamp = getCurrentTime();

        if (currentFeature == null) {
            printf(TEMPLATE_TEST_SUITE_STARTED, timestamp, feature.uri() + ":" + feature.lineNumber(), feature.description());
            printf(TEMPLATE_TEST_SUITE_STARTED, timestamp, scenario.uri() + ":" + scenario.lineNumber(), scenario.description());

        }

        if (currentFeature != null && !Objects.equals(feature, currentFeature)) {
            printf(TEMPLATE_TEST_SUITE_FINISHED, timestamp, currentScenario.description());
            printf(TEMPLATE_TEST_SUITE_FINISHED, timestamp, currentFeature.description());

            printf(TEMPLATE_TEST_SUITE_STARTED, timestamp, feature.uri() + ":" + feature.lineNumber(), feature.description());
            printf(TEMPLATE_TEST_SUITE_STARTED, timestamp, scenario.uri() + ":" + scenario.lineNumber(), scenario.description());
            printf(TEMPLATE_SCENARIO_STARTED, timestamp);
        } else if (currentScenario != null && !Objects.equals(scenario, currentScenario)) {
            printf(TEMPLATE_TEST_SUITE_FINISHED, timestamp, currentScenario.description());

            printf(TEMPLATE_TEST_SUITE_STARTED, timestamp, scenario.uri() + ":" + scenario.lineNumber(), scenario.description());
            printf(TEMPLATE_SCENARIO_STARTED, timestamp);
        }

        printf(TEMPLATE_TEST_STARTED, timestamp, step.uri() + ":" + step.lineNumber(), step.description());

        currentFeature = feature;
        currentScenario = scenario;
    }

    @Override
    public void testFinished(Description description) throws Exception {
        StepInfo step = description.getAnnotation(StepInfo.class);
        String timestamp = getCurrentTime();
        printf(TEMPLATE_TEST_FINISHED, timestamp, timestamp, step.description());
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        Description description = failure.getDescription();
        StepInfo step = description.getAnnotation(StepInfo.class);
        String timestamp = getCurrentTime();
        failure.getException().printStackTrace();
        printf(TEMPLATE_TEST_FAILED, timestamp, "", escape(failure.getMessage()), step.description(), "error = 'true'");
        printf(TEMPLATE_SCENARIO_FAILED, timestamp);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        Description description = failure.getDescription();
        StepInfo step = description.getAnnotation(StepInfo.class);
        String timestamp = getCurrentTime();
        failure.getException().printStackTrace();
        printf(TEMPLATE_TEST_PENDING, step.description(), timestamp);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        StepInfo step = description.getAnnotation(StepInfo.class);
        String timestamp = getCurrentTime();
        printf(TEMPLATE_TEST_STARTED, timestamp, step.uri() + ":" + step.lineNumber(), step.description());
        printf(TEMPLATE_TEST_PENDING, step.description(), timestamp);
        //printf(TEMPLATE_SCENARIO_FAILED, timestamp);
    }
}
