package org.zuchini.junit;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.zuchini.junit.description.AnnotationHandler;
import org.zuchini.junit.description.FeatureInfo;
import org.zuchini.junit.description.ScenarioInfo;
import org.zuchini.runner.Context;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.Scope;
import org.zuchini.runner.SimpleScenarioStatement;

import java.lang.annotation.Annotation;

class SimpleScenarioRunner extends Runner {

    private final Context context;
    private final FeatureStatement featureStatement;
    private final SimpleScenarioStatement scenarioStatement;
    private final Description description;

    public SimpleScenarioRunner(Context context, FeatureStatement featureStatement, SimpleScenarioStatement scenarioStatement) throws InitializationError {
        this.context = context;
        this.featureStatement = featureStatement;
        this.scenarioStatement = scenarioStatement;
        this.description = DescriptionHelper.createScenarioDescription(scenarioStatement.getScenario(), getRunnerAnnotations());
    }

    private Annotation[] getRunnerAnnotations() {
        return new Annotation[] {
                AnnotationHandler.create(FeatureInfo.class, featureStatement.getFeature()),
                AnnotationHandler.create(ScenarioInfo.class, scenarioStatement.getScenario())
        };
    }

    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    public void run(RunNotifier notifier) {
        Scope scenarioScope = context.getScenarioScope();
        scenarioScope.begin();
        try {
            notifier.fireTestStarted(description);
            scenarioStatement.evaluate(context);
        } catch (AssumptionViolatedException ex) {
            notifier.fireTestAssumptionFailed(new Failure(description, ex));
        } catch (Throwable throwable) {
            notifier.fireTestFailure(new Failure(description, throwable));
        } finally {
            notifier.fireTestFinished(description);
            scenarioScope.end();
        }
    }

    @Override
    public int testCount() {
        return 1;
    }
}
