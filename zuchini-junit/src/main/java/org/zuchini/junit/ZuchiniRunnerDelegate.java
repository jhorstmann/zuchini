package org.zuchini.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.zuchini.runner.Context;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.Scope;
import org.zuchini.runner.World;
import org.zuchini.runner.WorldBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class ZuchiniRunnerDelegate extends ParentRunner<FeatureRunner> implements Context {
    private final ZuchiniOptions options;
    private final World world;
    private final List<FeatureRunner> children;
    private final Description description;

    public ZuchiniRunnerDelegate(Class<?> testClass, Scope globalScope, Scope scenarioScope) throws InitializationError, IOException {
        super(testClass);
        this.options = testClass.getAnnotation(ZuchiniOptions.class);
        this.world = buildWorld(testClass, globalScope, scenarioScope);
        this.children = buildChildren(testClass, world.getFeatureStatements());
        this.description = DescriptionHelper.createRunnerDescription(testClass, getName(), children, getRunnerAnnotations());
    }

    private List<FeatureRunner> buildChildren(Class<?> testClass, List<FeatureStatement> featureStatements) throws InitializationError {
        final boolean reportIndividualSteps = options.reportIndividualSteps();
        final List<FeatureRunner> children = new ArrayList<>(featureStatements.size());

        for (FeatureStatement featureStatement : featureStatements) {
            children.add(new FeatureRunner(testClass, world, featureStatement, reportIndividualSteps));
        }

        return children;
    }

    private World buildWorld(Class<?> testClass, Scope globalScope, Scope scenarioScope) throws IOException {
        final List<String> featurePackages = asList(options.featurePackages());
        final List<String> stepDefinitionPackages = asList(options.stepDefinitionPackages());

        return new WorldBuilder(testClass.getClassLoader())
                .withFeaturePackages(featurePackages)
                .withStepDefinitionPackages(stepDefinitionPackages)
                .withGlobalScope(globalScope)
                .withScenarioScope(scenarioScope)
                .buildWorld();
    }

    private List<RunListener> buildListeners() {
        if (options.listeners() == null || options.listeners().length == 0) {
            return Collections.emptyList();
        } else {
            final List<RunListener> listeners = new ArrayList<>(options.listeners().length);
            for (Class<? extends RunListener> listenerClass : options.listeners()) {
                final RunListener listener = getGlobalScope().getObject(listenerClass);
                listeners.add(listener);
            }
            return listeners;
        }
    }

    @Override
    public Scope getGlobalScope() {
        return world.getGlobalScope();
    }

    @Override
    public Scope getScenarioScope() {
        return world.getScenarioScope();
    }

    @Override
    protected String getName() {
        return "Zuchini " + Arrays.toString(options.featurePackages());
    }

    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    public void run(RunNotifier notifier) {
        final Description description = getDescription();
        final List<RunListener> listeners = buildListeners();
        for (RunListener listener : listeners) {
            try {
                listener.testRunStarted(description);
                notifier.addListener(listener);
            } catch (Exception e) {
                notifier.fireTestFailure(new Failure(Description.TEST_MECHANISM, e));
            }
        }

        super.run(notifier);
    }

    @Override
    public List<FeatureRunner> getChildren() {
        return children;
    }

    @Override
    public Description describeChild(FeatureRunner child) {
        return child.getDescription();
    }

    @Override
    public void runChild(FeatureRunner child, RunNotifier notifier) {
        child.run(notifier);
    }
}
