package org.zuchini.junit;

import org.junit.runner.Describable;
import org.junit.runner.Description;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

class DescriptionHelper {

    private static final AtomicLong COUNTER = new AtomicLong();
    private DescriptionHelper() {

    }

    static Description createDescription(String uri, int lineNumber, String keyword, String description,
                                         Annotation... annotations) {
        return createDescription(uri, lineNumber, keyword, description, Collections.<Describable>emptyList(), annotations);
    }

    static Description createDescription(String uri, int lineNumber, String keyword, String description,
                                         List<? extends Describable> children, Annotation... annotations) {
        // HACK: JUnit by default uses the display name to track already executed or ignored tests,
        // HACK: Work around that by generating really unique ids
        Long uniqueId = COUNTER.incrementAndGet();
        String displayName = String.format("%s %s [%s:%d]", keyword, description, uri, lineNumber);
        Description suiteDescription = Description.createSuiteDescription(displayName, uniqueId, annotations);
        for (Describable child : children) {
            suiteDescription.addChild(child.getDescription());
        }
        return suiteDescription;
    }
}
