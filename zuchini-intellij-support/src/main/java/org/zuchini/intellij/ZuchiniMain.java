package org.zuchini.intellij;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.zuchini.junit.description.FeatureInfo;
import org.zuchini.junit.description.OutlineInfo;
import org.zuchini.junit.description.ScenarioInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ZuchiniMain {

    public static void main(String[] args) throws Throwable {
        List<File> featureFiles = new ArrayList<>();
        List<String> glue = new ArrayList<>();
        String name = null;

        for (int i = 0; i < args.length; i++) {
            if ("--glue".equals(args[i])) {
                glue.add(args[++i]);
            } else if ("--format".equals(args[i])) {
                ++i;
            } else if ("--name".equals(args[i])) {
                name = args[++i];

            } else if (!args[i].startsWith("--")) {
                featureFiles.add(new File(args[i]));
            }
        }

        if (glue.isEmpty()) {
            glue.add("");
        }
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        // Exit the jvm to stop any running non-daemon threads, for example embedded servlet containers
        // Use same exit codes as IntelliJ JUnit4IdeaTestRunner
        // See https://github.com/JetBrains/intellij-community/blob/7fa96faf725f9ee605444bacb794c21906a3e699/plugins/junit_rt/src/com/intellij/junit4/JUnit4IdeaTestRunner.java#L69
        try {
            Result result = run(cl, featureFiles, glue, name);
            System.exit(result.wasSuccessful() ? 0 : -1);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.exit(-2);
        }
    }

    public static Result run(final ClassLoader cl, final List<File> featureFiles, final List<String> glue,
                           final String name) throws Throwable {

        RunnerScanner runnerScanner = new RunnerScanner(cl, glue);
        runnerScanner.scan();
        Class<?> runner = runnerScanner.getRunner();

        Request request = Request.classes(runner);
        if (!featureFiles.isEmpty()) {
            request = request.filterWith(new Filter() {
                @Override
                public boolean shouldRun(Description description) {
                    FeatureInfo feature = description.getAnnotation(FeatureInfo.class);
                    if (feature == null) {
                        return true;
                    } else  {
                        String featureUri = feature.uri();
                        for (File file : featureFiles) {
                            if (file.toURI().toASCIIString().endsWith(featureUri)) {
                                return true;
                            }
                        }
                        return false;
                    }
                }

                @Override
                public String describe() {
                    return "Filter by files " + featureFiles.toString();
                }
            });
        }
        if (name != null) {
            final Pattern namePattern = Pattern.compile(name);
            request = request.filterWith(new Filter() {
                @Override
                public boolean shouldRun(Description description) {
                    ScenarioInfo scenario = description.getAnnotation(ScenarioInfo.class);
                    OutlineInfo outline = description.getAnnotation(OutlineInfo.class);
                    if (outline != null) {
                        // match against original outline name instead of nested scenarios whose names have parameter values appended
                        String outlineName = outline.name();
                        return namePattern.matcher(outlineName).matches();
                    } else if (scenario == null) {
                        return true;
                    } else {
                        String scenarioName = scenario.name();
                        return namePattern.matcher(scenarioName).matches();
                    }
                }

                @Override
                public String describe() {
                    return "Filter by name [" + name + "]";
                }
            });
        }


        JUnitCore core = new JUnitCore();
        core.addListener(new EnterTheMatrixRunListener(System.err));
        Result result = core.run(request);

        return result;
    }
}
