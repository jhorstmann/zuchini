package org.zuchini.cucumber.core;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.manipulation.Filter;
import org.zuchini.junit.description.ScenarioInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ZuchiniMain {


    public static void main(String[] args) throws Throwable {
        List<File> features = new ArrayList<>();
        List<String> stepDefinitionPackages = new ArrayList<>();
        String name = null;
        for (int i = 0; i < args.length; i++) {

            if ("--glue".equals(args[i])) {
                stepDefinitionPackages.add(args[++i]);
            } else if ("--format".equals(args[i])) {
                ++i;
            } else if ("--name".equals(args[i])) {
                name = args[++i];

            } else if (!args[i].startsWith("--")) {
                String feature = args[i].replaceFirst(":\\d+$", "");
                features.add(new File(feature));
            }
        }

        if (stepDefinitionPackages.isEmpty()) {
            stepDefinitionPackages.add("");
        }
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        run(cl, features, stepDefinitionPackages, name);
    }

    public static void run(ClassLoader cl, List<File> features, List<String> stepDefinitionPackages,
                           final String name) throws Throwable {

        RunnerScanner runnerScanner = new RunnerScanner(cl, stepDefinitionPackages);
        runnerScanner.scan();
        Class<?> runner = runnerScanner.getRunner();

        Request request = Request.classes(runner);
        if (name != null) {
            request = request.filterWith(new Filter() {
                @Override
                public boolean shouldRun(Description description) {
                    ScenarioInfo scenario = description.getAnnotation(ScenarioInfo.class);
                    return scenario == null || scenario.description().equals(name);
                }

                @Override
                public String describe() {
                    return name;
                }
            });
        }

        JUnitCore core = new JUnitCore();
        core.addListener(new EnterTheMatrixRunListener(System.err));
        core.run(request);

    }
}
