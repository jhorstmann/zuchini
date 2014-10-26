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
import org.zuchini.model.Scenario;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.Scope;
import org.zuchini.runner.SimpleScenarioStatement;

class SimpleScenarioRunner extends Runner {

    private final Scope scope;
    private final FeatureStatement featureStatement;
    private final SimpleScenarioStatement scenarioStatement;

    public SimpleScenarioRunner(Scope scope, FeatureStatement featureStatement, SimpleScenarioStatement scenarioStatement) throws InitializationError {
        this.scope = scope;
        this.featureStatement = featureStatement;
        this.scenarioStatement = scenarioStatement;
    }

    @Override
    public Description getDescription() {
        Scenario scenario = this.scenarioStatement.getScenario();
        ScenarioInfo scenarioInfo = AnnotationHandler.create(ScenarioInfo.class, scenario);
        FeatureInfo featureInfo = AnnotationHandler.create(FeatureInfo.class, featureStatement.getFeature());
        return DescriptionHelper.createDescription(scenario.getUri(), scenario.getLineNumber(), scenario.getKeyword(),
                scenario.getDescription(), featureInfo, scenarioInfo);
    }

    @Override
    public void run(RunNotifier notifier) {
        Description description = getDescription();
        scope.begin();
        try {
            notifier.fireTestStarted(description);
            scenarioStatement.evaluate(scope);
            notifier.fireTestFinished(description);
        } catch (AssumptionViolatedException ex) {
            notifier.fireTestAssumptionFailed(new Failure(description, ex));
        } catch (Throwable throwable) {
            notifier.fireTestFailure(new Failure(description, throwable));
        } finally {
            scope.end();
        }
    }

    @Override
    public int testCount() {
        return 1;
    }
}
