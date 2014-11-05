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
import java.util.concurrent.atomic.AtomicInteger;

class DescriptionHelper {

    private static final AtomicInteger COUNTER = new AtomicInteger();
    private DescriptionHelper() {

    }

    static Description createUniqueDescription(String displayName, List<? extends Describable> children,
                                               Annotation... annotations) {
        // HACK: JUnit by default uses the display name to track already executed or ignored tests,
        // HACK: Work around that by generating really unique ids
        int uniqueId = COUNTER.incrementAndGet();

        Description suiteDescription = Description.createSuiteDescription(displayName, uniqueId, annotations);
        for (Describable child : children) {
            suiteDescription.addChild(child.getDescription());
        }
        return suiteDescription;
    }

    static Description createUniqueDescription(String uri, int lineNumber, String keyword, String description,
                                                       List<? extends Describable> children, Annotation... annotations) {
        String displayName = String.format("%s %s [%s:%d]", keyword, description, uri, lineNumber);

        return createUniqueDescription(displayName, children, annotations);
    }

    static Description createFeatureDescription(Feature feature, List<? extends Describable> children, Annotation... annotations) {
        return createUniqueDescription(feature.getUri(), feature.getLineNumber(), feature.getKeyword(),
                feature.getDescription(), children, annotations);
    }

    static Description createOutlineDescription(Outline outline, List<? extends Describable> children, Annotation... annotations) {
        return createUniqueDescription(outline.getUri(), outline.getLineNumber(), outline.getKeyword(),
                outline.getDescription(), children, annotations);
    }

    static Description createScenarioDescription(Scenario scenario, List<? extends Describable> children, Annotation... annotations) {
        return createUniqueDescription(scenario.getUri(), scenario.getLineNumber(), scenario.getKeyword(),
                scenario.getDescription(), children, annotations);
    }

    static Description createScenarioDescription(Scenario scenario, Annotation... annotations) {
        return createScenarioDescription(scenario, Collections.<Describable>emptyList(), annotations);
    }

    static Description createStepDescription(Step step, Annotation... annotations) {
        return createUniqueDescription(step.getUri(), step.getLineNumber(), step.getKeyword(),
                step.getDescription(), Collections.<Describable>emptyList(), annotations);
    }

}
