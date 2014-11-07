package org.zuchini.cucumber.core;

import org.junit.runner.RunWith;
import org.zuchini.junit.ZuchiniOptions;
import org.zuchini.runner.internal.ClasspathScanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RunnerScanner extends ClasspathScanner {
    private final List<Class<?>> runner = new ArrayList<>();

    public RunnerScanner(ClassLoader classLoader, List<String> packageNames) {
        super(classLoader, packageNames, ".class");
    }

    @Override
    protected void handleResource(String classResource) throws IOException {
        String className = classResource.replace('/', '.').replaceFirst(".class$", "");
        try {
            Class<?> clazz = Class.forName(className, false, classLoader);
            if (clazz.getAnnotation(RunWith.class) != null && clazz.getAnnotation(ZuchiniOptions.class) != null) {
                runner.add(clazz);
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
        }
    }

    public Class<?> getRunner() {
        if (runner.isEmpty()) {
            throw new IllegalStateException("No junit entry point found");
        } else if (runner.size() > 1) {
            throw new IllegalStateException("More than one junit entry point found");
        } else {
            return runner.get(0);
        }
    }
}
