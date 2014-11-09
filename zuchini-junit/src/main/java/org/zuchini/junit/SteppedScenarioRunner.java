package org.zuchini.junit;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Describable;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.zuchini.junit.description.AnnotationHandler;
import org.zuchini.junit.description.FeatureInfo;
import org.zuchini.junit.description.ScenarioInfo;
import org.zuchini.junit.description.StepInfo;
import org.zuchini.model.Step;
import org.zuchini.runner.Context;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.Scope;
import org.zuchini.runner.SimpleScenarioStatement;
import org.zuchini.runner.StepStatement;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class SteppedScenarioRunner extends Runner {

    static class IgnoreRemainingStepsException extends Exception {
        static final IgnoreRemainingStepsException INSTANCE = new IgnoreRemainingStepsException();
        private IgnoreRemainingStepsException() {
        }
    }

    static class DescribedStepStatement implements Describable {
        private final StepStatement stepStatement;
        private final Description description;

        DescribedStepStatement(StepStatement stepStatement, Description description) {
            this.stepStatement = stepStatement;
            this.description = description;
        }

        StepStatement getStepStatement() {
            return stepStatement;
        }

        public Description getDescription() {
            return description;
        }
    }

    private final Context context;
    private final FeatureStatement featureStatement;
    private final SimpleScenarioStatement scenarioStatement;
    private final List<DescribedStepStatement> children;
    private final Description description;

    public SteppedScenarioRunner(Context context, FeatureStatement featureStatement, SimpleScenarioStatement scenarioStatement) throws InitializationError {
        this.context = context;
        this.featureStatement = featureStatement;
        this.scenarioStatement = scenarioStatement;
        this.children = buildChildren();
        this.description = DescriptionHelper.createScenarioDescription(scenarioStatement.getScenario(), children,
                getRunnerAnnotations());
    }

    private List<DescribedStepStatement> buildChildren() {
        List<StepStatement> steps = scenarioStatement.getSteps();
        List<DescribedStepStatement> result = new ArrayList<>(steps.size());
        for (StepStatement stepStatement : steps) {
            Step step = stepStatement.getStep();
            result.add(new DescribedStepStatement(stepStatement,
                    DescriptionHelper.createStepDescription(step, getStepAnnotations(step))));
        }
        return result;
    }

    private Annotation[] getStepAnnotations(Step step) {
        return new Annotation[]{
                AnnotationHandler.create(FeatureInfo.class, featureStatement.getFeature()),
                AnnotationHandler.create(ScenarioInfo.class, scenarioStatement.getScenario()),
                AnnotationHandler.create(StepInfo.class, step)
        };
    }

    private Annotation[] getRunnerAnnotations() {
        return new Annotation[]{
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
            Iterator<DescribedStepStatement> it = children.iterator();
            try {
                while (it.hasNext()) {
                    DescribedStepStatement describedStepStatement = it.next();
                    Description stepDescription = describedStepStatement.getDescription();
                    StepStatement stepStatement = describedStepStatement.getStepStatement();
                    notifier.fireTestStarted(stepDescription);
                    try {
                        stepStatement.evaluate(context);
                    } catch (AssumptionViolatedException ex) {
                        notifier.fireTestAssumptionFailed(new Failure(stepDescription, ex));
                        throw IgnoreRemainingStepsException.INSTANCE;
                    } catch (Throwable throwable) {
                        notifier.fireTestFailure(new Failure(stepDescription, throwable));
                        throw IgnoreRemainingStepsException.INSTANCE;
                    } finally {
                        notifier.fireTestFinished(stepDescription);
                    }
                }
            } catch (IgnoreRemainingStepsException e) {
                while (it.hasNext()) {
                    DescribedStepStatement describedStepStatement = it.next();
                    Description stepDescription = describedStepStatement.getDescription();
                    notifier.fireTestIgnored(stepDescription);
                }
            }
        } finally {
            scenarioScope.end();
        }
    }

    @Override
    public int testCount() {
        return scenarioStatement.getSteps().size();
    }
}
