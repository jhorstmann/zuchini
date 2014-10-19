package org.zuchini.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.zuchini.junit.description.AnnotationHandler;
import org.zuchini.junit.description.FeatureInfo;
import org.zuchini.junit.description.OutlineInfo;
import org.zuchini.model.Outline;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.OutlineStatement;
import org.zuchini.runner.ScenarioScope;
import org.zuchini.runner.ScenarioStatement;
import org.zuchini.runner.SimpleScenarioStatement;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

class OutlineRunner extends ZuchiniParentRunner<ScenarioRunner> {
    private final FeatureStatement featureStatement;
    private final OutlineStatement outlineStatement;
    private final List<ScenarioRunner> children;

    public OutlineRunner(Class<?> testClass, ScenarioScope scope, FeatureStatement featureStatement, OutlineStatement outlineStatement) throws InitializationError {
        super(testClass);
        this.outlineStatement = outlineStatement;
        this.featureStatement = featureStatement;
        this.children = buildChildren(testClass, scope, featureStatement, outlineStatement);
    }

    private static List<ScenarioRunner> buildChildren(Class<?> testClass, ScenarioScope scope, FeatureStatement featureStatement, OutlineStatement outline) throws InitializationError {
        List<SimpleScenarioStatement> scenarios = outline.getScenarios();
        List<ScenarioRunner> children = new ArrayList<>(scenarios.size());
        for (ScenarioStatement scenario : scenarios) {
            children.add(new ScenarioRunner(testClass, scope, featureStatement, (SimpleScenarioStatement) scenario));
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
    protected List<ScenarioRunner> getChildren() {
        return children;
    }

    @Override
    protected Description describeChild(ScenarioRunner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(ScenarioRunner child, RunNotifier notifier) {
        child.run(notifier);
    }
}
