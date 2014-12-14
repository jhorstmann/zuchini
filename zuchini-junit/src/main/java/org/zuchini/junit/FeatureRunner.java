package org.zuchini.junit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.zuchini.junit.description.AnnotationHandler;
import org.zuchini.junit.description.FeatureInfo;
import org.zuchini.runner.Context;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.OutlineStatement;
import org.zuchini.runner.ScenarioStatement;
import org.zuchini.runner.SimpleScenarioStatement;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

class FeatureRunner extends ParentRunner<Runner> {

    private final FeatureStatement featureStatement;
    private final List<Runner> children;
    private final Description description;

    public FeatureRunner(Class<?> testClass, Context context, FeatureStatement featureStatement, boolean reportIndividualSteps) throws InitializationError {
        super(testClass);
        this.featureStatement = featureStatement;
        this.children = buildChildren(testClass, context, featureStatement, reportIndividualSteps);
        this.description = DescriptionHelper.createFeatureDescription(testClass, featureStatement.getFeature(), children,
                getRunnerAnnotations());
    }

    private static List<Runner> buildChildren(Class<?> testClass, Context context, FeatureStatement featureStatement, boolean reportIndividualSteps) throws InitializationError {
        List<? extends ScenarioStatement> scenarios = featureStatement.getScenarios();
        List<Runner> children = new ArrayList<>(scenarios.size());
        for (ScenarioStatement scenario : scenarios) {
            if (scenario instanceof OutlineStatement) {
                children.add(new OutlineRunner(testClass, context, featureStatement, (OutlineStatement)scenario, reportIndividualSteps));
            } else if (scenario instanceof SimpleScenarioStatement) {
                if (reportIndividualSteps) {
                    children.add(new SteppedScenarioRunner(testClass, context, featureStatement, (SimpleScenarioStatement) scenario));
                } else {
                    children.add(new SimpleScenarioRunner(testClass, context, featureStatement, (SimpleScenarioStatement) scenario));
                }
            } else {
                throw new IllegalStateException("Unknown scenario type [" + scenario.getClass().getName() + "]");
            }
        }
        return children;
    }

    @Override
    protected Annotation[] getRunnerAnnotations() {
        return new Annotation[]{
                AnnotationHandler.create(FeatureInfo.class, featureStatement.getFeature()),
        };
    }

    @Override
    public Description getDescription() {
        return description;
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
