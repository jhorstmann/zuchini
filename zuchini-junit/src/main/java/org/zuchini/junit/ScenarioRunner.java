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
import org.zuchini.junit.description.StepInfo;
import org.zuchini.model.Feature;
import org.zuchini.model.Scenario;
import org.zuchini.model.Step;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.ScenarioScope;
import org.zuchini.runner.SimpleScenarioStatement;
import org.zuchini.runner.StepStatement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ScenarioRunner extends Runner {

    static class DescribedStepStatement {
        private final StepStatement stepStatement;
        private final Description description;

        DescribedStepStatement(StepStatement stepStatement, Description description) {
            this.stepStatement = stepStatement;
            this.description = description;
        }

        StepStatement getStepStatement() {
            return stepStatement;
        }

        Description getDescription() {
            return description;
        }
    }

    private final Class<?> testClass;
    private final ScenarioScope scope;
    private final FeatureStatement featureStatement;
    private final SimpleScenarioStatement scenarioStatement;
    private final List<DescribedStepStatement> children;

    public ScenarioRunner(Class<?> testClass, ScenarioScope scope, FeatureStatement featureStatement, SimpleScenarioStatement scenarioStatement) throws InitializationError {
        this.testClass = testClass;
        this.scope = scope;
        this.featureStatement = featureStatement;
        this.scenarioStatement = scenarioStatement;
        this.children = buildChildren(featureStatement, scenarioStatement);
    }

    private static List<DescribedStepStatement> buildChildren(FeatureStatement featureStatement, SimpleScenarioStatement scenarioStatement) {
        List<StepStatement> steps = scenarioStatement.getSteps();
        List<DescribedStepStatement> result = new ArrayList<>(steps.size());
        for (StepStatement step : steps) {
            result.add(new DescribedStepStatement(step, buildStepDescription(featureStatement, scenarioStatement, step)));
        }
        return result;
    }

    private static Description buildStepDescription(FeatureStatement featureStatement, SimpleScenarioStatement scenarioStatement, StepStatement stepStatement) {
        Feature feature = featureStatement.getFeature();
        Scenario scenario = scenarioStatement.getScenario();
        Step step = stepStatement.getStep();
        String location = step.getUri() + ":" + step.getLineNumber();
        String name = step.getKeyword() + " " + step.getDescription();
        ScenarioInfo scenarioInfo = AnnotationHandler.create(ScenarioInfo.class, scenario);
        FeatureInfo featureInfo = AnnotationHandler.create(FeatureInfo.class, feature);
        StepInfo stepInfo = AnnotationHandler.create(StepInfo.class, step);
        Description description = Description.createTestDescription(location, name, featureInfo, scenarioInfo, stepInfo);
        return description;
    }

    public String getName() {
        Scenario scenario = this.scenarioStatement.getScenario();
        return scenario.getKeyword() + " " + scenario.getDescription();
    }

    public String getLocation() {
        Scenario scenario = this.scenarioStatement.getScenario();
        return scenario.getUri() + ":" + scenario.getLineNumber();
    }

    @Override
    public Description getDescription() {
        Scenario scenario = this.scenarioStatement.getScenario();
        ScenarioInfo scenarioInfo = AnnotationHandler.create(ScenarioInfo.class, scenario);
        FeatureInfo featureInfo = AnnotationHandler.create(FeatureInfo.class, featureStatement.getFeature());
        String location = getLocation();
        String name = getName();
        Description description = Description.createSuiteDescription(name + " [" + location + "]", featureInfo, scenarioInfo);
        for (DescribedStepStatement child : children) {
            description.addChild(child.getDescription());
        }
        return description;
    }

    @Override
    public void run(RunNotifier notifier) {
        scope.begin();
        try {
            Iterator<DescribedStepStatement> it = children.iterator();
            try {
                while (it.hasNext()) {
                    DescribedStepStatement describedStepStatement = it.next();
                    Description stepDescription = describedStepStatement.getDescription();
                    StepStatement stepStatement = describedStepStatement.getStepStatement();
                    notifier.fireTestStarted(stepDescription);
                    try {
                        stepStatement.evaluate(scope);
                        notifier.fireTestFinished(stepDescription);
                    } catch (AssumptionViolatedException ex) {
                        notifier.fireTestAssumptionFailed(new Failure(stepDescription, ex));
                        throw ex;
                    } catch (Throwable throwable) {
                        notifier.fireTestFailure(new Failure(stepDescription, throwable));
                        throw throwable;
                    }
                }
            } catch (Throwable throwable) {
                while (it.hasNext()) {
                    DescribedStepStatement describedStepStatement = it.next();
                    Description stepDescription = describedStepStatement.getDescription();
                    notifier.fireTestIgnored(stepDescription);
                }
            }
        } finally {
            scope.end();
        }
    }

    @Override
    public int testCount() {
        return scenarioStatement.getSteps().size();
    }
}
