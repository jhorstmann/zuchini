package org.zuchini.junit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.zuchini.junit.description.AnnotationHandler;
import org.zuchini.junit.description.FeatureInfo;
import org.zuchini.junit.description.OutlineInfo;
import org.zuchini.runner.Context;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.OutlineStatement;
import org.zuchini.runner.ScenarioStatement;
import org.zuchini.runner.SimpleScenarioStatement;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

class OutlineRunner extends ParentRunner<Runner> {
    private final FeatureStatement featureStatement;
    private final OutlineStatement outlineStatement;
    private final List<Runner> children;
    private final Description description;

    public OutlineRunner(Class<?> testClass, Context context, FeatureStatement featureStatement, OutlineStatement outlineStatement, boolean reportIndividualSteps) throws InitializationError {
        super(testClass);
        this.outlineStatement = outlineStatement;
        this.featureStatement = featureStatement;
        this.children = buildChildren(context, featureStatement, outlineStatement, reportIndividualSteps);
        this.description = DescriptionHelper.createOutlineDescription(outlineStatement.getOutline(), children,
                getRunnerAnnotations());
    }

    private static List<Runner> buildChildren(Context context, FeatureStatement featureStatement, OutlineStatement outline, boolean reportIndividualSteps) throws InitializationError {
        List<SimpleScenarioStatement> scenarios = outline.getScenarios();
        List<Runner> children = new ArrayList<>(scenarios.size());
        for (ScenarioStatement scenario : scenarios) {
            if (reportIndividualSteps) {
                children.add(new SteppedScenarioRunner(context, featureStatement, (SimpleScenarioStatement) scenario));
            } else {
                children.add(new SimpleScenarioRunner(context, featureStatement, (SimpleScenarioStatement) scenario));
            }
        }
        return children;
    }

    @Override
    protected Annotation[] getRunnerAnnotations() {
        return new Annotation[]{
                AnnotationHandler.create(FeatureInfo.class, featureStatement.getFeature()),
                AnnotationHandler.create(OutlineInfo.class, outlineStatement.getOutline()),
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
