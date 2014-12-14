package org.zuchini.junit;

import org.junit.runner.Describable;
import org.junit.runner.Description;
import org.zuchini.model.Feature;
import org.zuchini.model.Outline;
import org.zuchini.model.Scenario;
import org.zuchini.model.Step;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

class DescriptionHelper {

    private DescriptionHelper() {

    }

    private static Description createUniqueDescription(Class<?> runner, String displayName, List<? extends Describable> children,
                                                       Annotation... annotations) {
        final Description description;

        if (children.isEmpty()) {
            description = Description.createTestDescription(runner, displayName, annotations);
        } else {
            description = Description.createSuiteDescription(displayName, annotations);
        }
        for (Describable child : children) {
            description.addChild(child.getDescription());
        }
        return description;
    }

    private static Description createUniqueDescription(Class<?> runner, String uri, int lineNumber, String keyword, String description,
                                                       List<? extends Describable> children, Annotation... annotations) {
        String displayName = String.format("%s %s [%s:%d]", keyword, description, uri, lineNumber);

        return createUniqueDescription(runner, displayName, children, annotations);
    }

    static Description createRunnerDescription(Class<?> runner, String displayName, List<? extends Describable> children, Annotation... annotations) {
        return createUniqueDescription(runner, displayName, children, annotations);
    }

    static Description createFeatureDescription(Class<?> runner, Feature feature, List<? extends Describable> children, Annotation... annotations) {
        return createUniqueDescription(runner, feature.getUri(), feature.getLineNumber(), feature.getKeyword(),
                feature.getDescription(), children, annotations);
    }

    static Description createOutlineDescription(Class<?> runner, Outline outline, List<? extends Describable> children, Annotation... annotations) {
        return createUniqueDescription(runner, outline.getUri(), outline.getLineNumber(), outline.getKeyword(),
                outline.getDescription(), children, annotations);
    }

    static Description createScenarioDescription(Class<?> runner, Scenario scenario, List<? extends Describable> children, Annotation... annotations) {
        return createUniqueDescription(runner, scenario.getUri(), scenario.getLineNumber(), scenario.getKeyword(),
                scenario.getDescription(), children, annotations);
    }

    static Description createScenarioDescription(Class<?> runner, Scenario scenario, Annotation... annotations) {
        return createScenarioDescription(runner, scenario, Collections.<Describable>emptyList(), annotations);
    }

    static Description createStepDescription(Class<?> runner, Step step, Annotation... annotations) {
        return createUniqueDescription(runner, step.getUri(), step.getLineNumber(), step.getKeyword(),
                step.getDescription(), Collections.<Describable>emptyList(), annotations);
    }

}
