package org.zuchini.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.Scope;
import org.zuchini.runner.World;
import org.zuchini.runner.WorldBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class Zuchini extends ParentRunner<FeatureRunner> {

    private final List<FeatureRunner> children;
    private final ZuchiniOptions options;
    private final World world;

    public Zuchini(Class<?> testClass) throws InitializationError, IOException, IllegalAccessException, InstantiationException {
        super(testClass);
        this.options = testClass.getAnnotation(ZuchiniOptions.class);
        this.world = buildWorld(testClass, asList(options.featurePackages()), asList(options.stepDefinitionPackages()));
        this.children = buildChildren(testClass, world, options);
    }

    private static World buildWorld(Class<?> testClass, List<String> featurePackages, List<String> stepDefinitionPackages) throws IOException {
        return new WorldBuilder(testClass.getClassLoader())
                .withDefaultConverterConfiguration()
                .withFeaturePackages(featurePackages)
                .withStepDefinitionPackages(stepDefinitionPackages)
                .buildWorld();
    }

    private static List<FeatureRunner> buildChildren(Class<?> testClass, World world, ZuchiniOptions options) throws
            InstantiationException,
            IllegalAccessException,
            IOException, InitializationError {
        boolean reportIndividualSteps = options.reportIndividualSteps();

        List<FeatureStatement> featureStatements = world.getFeatureStatements();
        Scope scenarioScope = world.getScenarioScope();

        ArrayList<FeatureRunner> children = new ArrayList<>(featureStatements.size());
        for (FeatureStatement featureStatement : featureStatements) {
            children.add(new FeatureRunner(testClass, scenarioScope, featureStatement, reportIndividualSteps));
        }

        return children;
    }

    @Override
    public void run(RunNotifier notifier) {
        world.getGlobalScope().begin();
        try {
            super.run(notifier);
        } finally {
            world.getGlobalScope().end();
        }
    }

    @Override
    protected List<FeatureRunner> getChildren() {
        return children;
    }

    @Override
    protected Description describeChild(FeatureRunner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(FeatureRunner child, RunNotifier notifier) {
        child.run(notifier);
    }

}
