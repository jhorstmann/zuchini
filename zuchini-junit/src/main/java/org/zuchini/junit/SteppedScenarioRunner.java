package org.zuchini.junit;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Describable;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.zuchini.junit.description.*;
import org.zuchini.model.Step;
import org.zuchini.runner.*;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
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

    private final Class<?> testClass;
    private final Context context;
    private final FeatureStatement featureStatement;
    private final SimpleScenarioStatement scenarioStatement;
    @Nullable
    private final OutlineStatement outlineStatement;
    private final List<DescribedStepStatement> children;
    private final Description description;

    public SteppedScenarioRunner(Class<?> testClass, Context context, FeatureStatement featureStatement, SimpleScenarioStatement scenarioStatement, @Nullable OutlineStatement outlineStatement) throws InitializationError {
        this.testClass = testClass;
        this.context = context;
        this.featureStatement = featureStatement;
        this.scenarioStatement = scenarioStatement;
        this.outlineStatement = outlineStatement;
        this.children = buildChildren();
        this.description = DescriptionHelper.createScenarioDescription(testClass, scenarioStatement.getScenario(), children,
                getRunnerAnnotations());
    }

    private List<DescribedStepStatement> buildChildren() {
        final List<StepStatement> steps = scenarioStatement.getSteps();
        final List<DescribedStepStatement> result = new ArrayList<>(steps.size());
        for (StepStatement stepStatement : steps) {
            final Step step = stepStatement.getStep();
            result.add(new DescribedStepStatement(stepStatement,
                    DescriptionHelper.createStepDescription(testClass, step, getStepAnnotations(step))));
        }
        return result;
    }

    private Annotation[] getStepAnnotations(Step step) {
        final Annotation[] runnerAnnotations = getRunnerAnnotations();
        final Annotation[] stepAnnotations = Arrays.copyOf(runnerAnnotations, runnerAnnotations.length + 1);
        stepAnnotations[stepAnnotations.length-1] = AnnotationHandler.create(StepInfo.class, step);
        return stepAnnotations;
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
        final Iterator<DescribedStepStatement> it = children.iterator();

        try {
            while (it.hasNext()) {
                final DescribedStepStatement describedStepStatement = it.next();
                final Description stepDescription = describedStepStatement.getDescription();
                final Statement stepStatement = describedStepStatement.getStepStatement();

                try {
                    notifier.fireTestStarted(stepDescription);

                    evaluate(notifier, stepStatement, stepDescription);
                } finally {
                    notifier.fireTestFinished(stepDescription);
                }
            }
        } catch (IgnoreRemainingStepsException e) {
            while (it.hasNext()) {
                final DescribedStepStatement describedStepStatement = it.next();
                final Description stepDescription = describedStepStatement.getDescription();
                notifier.fireTestIgnored(stepDescription);
            }
        }
    }

    private void evaluate(final RunNotifier notifier, final Statement statement, final Description description) throws IgnoreRemainingStepsException {
        try {
            statement.evaluate(context);
        } catch (AssumptionViolatedException ex) {
            notifier.fireTestAssumptionFailed(new Failure(description, ex));
            throw IgnoreRemainingStepsException.INSTANCE;
        } catch (Throwable throwable) {
            notifier.fireTestFailure(new Failure(description, throwable));
            throw IgnoreRemainingStepsException.INSTANCE;
        }
    }

    @Override
    public int testCount() {
        return scenarioStatement.getSteps().size();
    }
}
