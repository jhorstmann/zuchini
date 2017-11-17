package org.zuchini.junit;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.zuchini.junit.description.AnnotationHandler;
import org.zuchini.junit.description.FeatureInfo;
import org.zuchini.junit.description.OutlineInfo;
import org.zuchini.junit.description.ScenarioInfo;
import org.zuchini.runner.*;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

class SimpleScenarioRunner extends Runner {

    private final Class<?> testClass;
    private final Context context;
    private final FeatureStatement featureStatement;
    private final SimpleScenarioStatement scenarioStatement;
    @Nullable
    private final OutlineStatement outlineStatement;
    private final Description description;

    public SimpleScenarioRunner(Class<?> testClass, Context context, FeatureStatement featureStatement, SimpleScenarioStatement scenarioStatement, @Nullable OutlineStatement outlineStatement) throws InitializationError {
        this.testClass = testClass;
        this.context = context;
        this.featureStatement = featureStatement;
        this.scenarioStatement = scenarioStatement;
        this.outlineStatement = outlineStatement;
        this.description = DescriptionHelper.createScenarioDescription(testClass, scenarioStatement.getScenario(), getRunnerAnnotations());
    }

    private Annotation[] getRunnerAnnotations() {
        final Annotation[] annotations = new Annotation[outlineStatement == null ? 2 : 3];
        annotations[0] = AnnotationHandler.create(FeatureInfo.class, featureStatement.getFeature());
        annotations[1] = AnnotationHandler.create(ScenarioInfo.class, scenarioStatement.getScenario());
        if (outlineStatement != null) {
            annotations[2] = AnnotationHandler.create(OutlineInfo.class, outlineStatement.getOutline());
        }
        return annotations;
    }

    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            notifier.fireTestStarted(description);
            scenarioStatement.evaluate(context);
        } catch (AssumptionViolatedException ex) {
            notifier.fireTestAssumptionFailed(new Failure(description, ex));
        } catch (Throwable throwable) {
            notifier.fireTestFailure(new Failure(description, throwable));
        } finally {
            notifier.fireTestFinished(description);
        }
    }

    @Override
    public int testCount() {
        return 1;
    }
}
