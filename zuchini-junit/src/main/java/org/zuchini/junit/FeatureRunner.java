package org.zuchini.junit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.zuchini.junit.description.AnnotationHandler;
import org.zuchini.junit.description.FeatureInfo;
import org.zuchini.model.Feature;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.OutlineStatement;
import org.zuchini.runner.ScenarioScope;
import org.zuchini.runner.ScenarioStatement;
import org.zuchini.runner.SimpleScenarioStatement;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

class FeatureRunner extends ZuchiniParentRunner<Runner> {

    private final FeatureStatement featureStatement;
    private final List<Runner> children;

    public FeatureRunner(Class<?> testClass, ScenarioScope scope, FeatureStatement featureStatement) throws InitializationError {
        super(testClass);
        this.featureStatement = featureStatement;
        this.children = buildChildren(testClass, scope, featureStatement);
    }

    private static List<Runner> buildChildren(Class<?> testClass, ScenarioScope scope, FeatureStatement featureStatement) throws InitializationError {
        List<? extends ScenarioStatement> scenarios = featureStatement.getScenarios();
        List<Runner> children = new ArrayList<>(scenarios.size());
        for (ScenarioStatement scenario : scenarios) {
            if (scenario instanceof OutlineStatement) {
                children.add(new OutlineRunner(testClass, scope, featureStatement, (OutlineStatement)scenario));
            } else if (scenario instanceof SimpleScenarioStatement) {
                children.add(new ScenarioRunner(testClass, scope, featureStatement, (SimpleScenarioStatement) scenario));
            } else {
                throw new IllegalStateException("Unknown scenario type [" + scenario.getClass().getName() + "]");
            }
        }
        return children;
    }

    @Override
    protected String getName() {
        Feature feature = featureStatement.getFeature();
        return feature.getKeyword() + " " + feature.getDescription();
    }

    @Override
    protected String getLocation() {
        Feature feature = featureStatement.getFeature();
        return feature.getUri() + ":" + feature.getLineNumber();
    }

    @Override
    protected Annotation[] getRunnerAnnotations() {
        return new Annotation[]{
                AnnotationHandler.create(FeatureInfo.class, featureStatement.getFeature()),
        };
    }

    @Override
    protected List<Runner> getChildren() {
        return children;
    }

    @Override
    protected Description describeChild(Runner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(Runner child, RunNotifier notifier) {
        child.run(notifier);
    }
}
