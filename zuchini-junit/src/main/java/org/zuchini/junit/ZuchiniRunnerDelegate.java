package org.zuchini.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.Scope;

import java.util.ArrayList;
import java.util.List;

public class ZuchiniRunnerDelegate {
    private final List<FeatureRunner> children;

    public ZuchiniRunnerDelegate(Class<?> testClass, List<FeatureStatement> featureStatements, Scope scenarioScope,
                                 boolean reportIndividualSteps) throws InitializationError {
        this.children = buildChildren(testClass, featureStatements, scenarioScope, reportIndividualSteps);
    }

    private static List<FeatureRunner> buildChildren(Class<?> testClass, List<FeatureStatement> featureStatements, Scope scenarioScope,
                                                     boolean reportIndividualSteps) throws InitializationError {

        ArrayList<FeatureRunner> children = new ArrayList<>(featureStatements.size());
        for (FeatureStatement featureStatement : featureStatements) {
            children.add(new FeatureRunner(testClass, scenarioScope, featureStatement, reportIndividualSteps));
        }

        return children;
    }

    public List<FeatureRunner> getChildren() {
        return children;
    }

    public Description describeChild(FeatureRunner child) {
        return child.getDescription();
    }

    public void runChild(FeatureRunner child, RunNotifier notifier) {
        child.run(notifier);
    }
}
