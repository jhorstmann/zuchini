package org.zuchini.intellij;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.manipulation.Filter;
import org.zuchini.junit.description.FeatureInfo;
import org.zuchini.junit.description.ScenarioInfo;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ZuchiniMain {

    public static void main(String[] args) throws Throwable {
        List<File> features = new ArrayList<>();
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
                features.add(new File(args[i]));
            }
        }

        if (glue.isEmpty()) {
            glue.add("");
        }
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        run(cl, features, glue, name);
    }

    public static void run(final ClassLoader cl, final List<File> features, final List<String> glue,
                           final String name) throws Throwable {

        RunnerScanner runnerScanner = new RunnerScanner(cl, glue);
        runnerScanner.scan();
        Class<?> runner = runnerScanner.getRunner();

        Request request = Request.classes(runner);
        if (!features.isEmpty()) {
            request = request.filterWith(new Filter() {
                @Override
                public boolean shouldRun(Description description) {
                    FeatureInfo feature = description.getAnnotation(FeatureInfo.class);
                    if (feature == null) {
                        return true;
                    } else  {
                        for (File file : features) {
                            URL url = cl.getResource(feature.uri());
                            if (url != null) {
                                String featureFile = url.getFile().replace("/target/test-classes/", "/src/test/resources/");
                                if (featureFile.startsWith(file.getPath())) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                }

                @Override
                public String describe() {
                    return "Filter by files " + features.toString();
                }
            });
        }
        if (name != null) {
            request = request.filterWith(new Filter() {
                @Override
                public boolean shouldRun(Description description) {
                    ScenarioInfo scenario = description.getAnnotation(ScenarioInfo.class);
                    // TODO: Does not work for scenario outlines with parameters appended to the name
                    return scenario == null || scenario.name().equals(name);
                }

                @Override
                public String describe() {
                    return "Filter by name [" + name + "]";
                }
            });
        }


        JUnitCore core = new JUnitCore();
        core.addListener(new EnterTheMatrixRunListener(System.err));
        core.run(request);
    }
}
