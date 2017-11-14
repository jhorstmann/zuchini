package org.zuchini.junit5;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.zuchini.model.Named;
import org.zuchini.runner.Context;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.GlobalScope;
import org.zuchini.runner.OutlineStatement;
import org.zuchini.runner.ScenarioStatement;
import org.zuchini.runner.SimpleScenarioStatement;
import org.zuchini.runner.StepStatement;
import org.zuchini.runner.ThreadLocalScope;
import org.zuchini.runner.World;
import org.zuchini.runner.WorldBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class ZuchiniTestFactory {
    private final Context context;
    private String[] featurePackages;
    private String[] stepDefinitionPackages;
    private boolean reportIndividualSteps;

    public ZuchiniTestFactory() {
        this(new ContextImpl(new GlobalScope(), new ThreadLocalScope()));
    }

    public ZuchiniTestFactory(Context context) {
        this.context = context;
    }

    public Stream<DynamicContainer> features() {
        return buildWorld().getFeatureStatements().stream().map(this::feature);
    }

    private World buildWorld() {
        try {
            return new WorldBuilder(Thread.currentThread().getContextClassLoader())
                    .withFeaturePackages(asList(this.featurePackages))
                    .withStepDefinitionPackages(asList(this.stepDefinitionPackages))
                    .withGlobalScope(context.getGlobalScope())
                    .withScenarioScope(context.getScenarioScope())
                    .buildWorld();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private DynamicContainer feature(FeatureStatement featureStatement) {
        return DynamicContainer.dynamicContainer(name(featureStatement.getFeature()),
                featureStatement.getScenarios().stream().map(this::scenario));
    }

    private DynamicNode scenario(ScenarioStatement scenarioStatement) {
        if (scenarioStatement instanceof SimpleScenarioStatement) {
            if (reportIndividualSteps) {
                return steps((SimpleScenarioStatement) scenarioStatement);
            } else {
                return DynamicTest.dynamicTest(((SimpleScenarioStatement) scenarioStatement).getScenario().getName(),
                        () -> scenarioStatement.evaluate(context));
            }
        } else if (scenarioStatement instanceof OutlineStatement) {
            final OutlineStatement outlineStatement = (OutlineStatement) scenarioStatement;
            return DynamicContainer.dynamicContainer(name(outlineStatement.getOutline()), outlineStatement.getScenarios().stream().map(this::scenario));
        } else {
            throw new UnsupportedOperationException("" + scenarioStatement.getClass().getName());
        }
    }

    private DynamicContainer steps(SimpleScenarioStatement scenarioStatement) {
        final List<StepStatement> steps = scenarioStatement.getSteps();

        return DynamicContainer.dynamicContainer(name(scenarioStatement.getScenario()),
                steps.stream()
                        .map(step -> DynamicTest.dynamicTest(name(step.getStep()), () -> step.evaluate(context)))
                        .sequential());
    }

    private String name(Named named) {
        return named.getKeyword() + " " + named.getName();
    }

    public ZuchiniTestFactory withFeaturePackages(String... featurePackages) {
        this.featurePackages = Arrays.copyOf(featurePackages, featurePackages.length);
        return this;
    }

    public ZuchiniTestFactory withStepDefinitionPackages(String... stepDefinitionPackages) {
        this.stepDefinitionPackages = Arrays.copyOf(stepDefinitionPackages, stepDefinitionPackages.length);
        return this;
    }

    public ZuchiniTestFactory withReportIndividualSteps(boolean reportIndividualSteps) {
        this.reportIndividualSteps = reportIndividualSteps;
        return this;
    }


}
