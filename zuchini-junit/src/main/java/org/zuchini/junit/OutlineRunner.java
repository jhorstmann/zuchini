package org.zuchini.junit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.zuchini.junit.description.AnnotationHandler;
import org.zuchini.junit.description.FeatureInfo;
import org.zuchini.junit.description.OutlineInfo;
import org.zuchini.model.Outline;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.OutlineStatement;
import org.zuchini.runner.Scope;
import org.zuchini.runner.ScenarioStatement;
import org.zuchini.runner.SimpleScenarioStatement;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

class OutlineRunner extends ZuchiniParentRunner<Runner> {
    private final FeatureStatement featureStatement;
    private final OutlineStatement outlineStatement;
    private final List<Runner> children;

    public OutlineRunner(Class<?> testClass, Scope scope, FeatureStatement featureStatement, OutlineStatement outlineStatement, boolean reportIndividualSteps) throws InitializationError {
        super(testClass);
        this.outlineStatement = outlineStatement;
        this.featureStatement = featureStatement;
        this.children = buildChildren(scope, featureStatement, outlineStatement, reportIndividualSteps);
    }

    private static List<Runner> buildChildren(Scope scope, FeatureStatement featureStatement, OutlineStatement outline, boolean reportIndividualSteps) throws InitializationError {
        List<SimpleScenarioStatement> scenarios = outline.getScenarios();
        List<Runner> children = new ArrayList<>(scenarios.size());
        for (ScenarioStatement scenario : scenarios) {
            if (reportIndividualSteps) {
                children.add(new SteppedScenarioRunner(scope, featureStatement, (SimpleScenarioStatement) scenario));
            } else {
                children.add(new SimpleScenarioRunner(scope, featureStatement, (SimpleScenarioStatement) scenario));
            }
        }
        return children;
    }

    @Override
    protected String getName() {
        Outline outline = outlineStatement.getOutline();
        return outline.getKeyword() + " " + outline.getDescription();
    }

    @Override
    public String getLocation() {
        Outline outline = outlineStatement.getOutline();
        return outline.getUri() + ":" + outline.getLineNumber();
    }

    @Override
    protected Annotation[] getRunnerAnnotations() {
        return new Annotation[]{
                AnnotationHandler.create(FeatureInfo.class, featureStatement.getFeature()),
                AnnotationHandler.create(OutlineInfo.class, outlineStatement.getOutline()),
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
