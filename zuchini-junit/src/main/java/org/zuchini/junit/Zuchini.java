package org.zuchini.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.GlobalScope;
import org.zuchini.runner.Scope;
import org.zuchini.runner.ThreadLocalScope;
import org.zuchini.runner.World;
import org.zuchini.runner.WorldBuilder;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;

public class Zuchini extends ParentRunner<FeatureRunner> {

    private final World world;
    private final ZuchiniRunnerDelegate delegate;
    private final Scope globalScope;
    private final Scope scenarioScope;

    public Zuchini(Class<?> testClass) throws InitializationError, IOException, IllegalAccessException, InstantiationException {
        super(testClass);
        ZuchiniOptions options = testClass.getAnnotation(ZuchiniOptions.class);
        this.world = buildWorld(testClass, asList(options.featurePackages()), asList(options.stepDefinitionPackages()));
        this.globalScope = new GlobalScope();
        this.scenarioScope = new ThreadLocalScope();
        List<FeatureStatement> featureStatements = world.getFeatureStatements();
        this.delegate = new ZuchiniRunnerDelegate(testClass, featureStatements, scenarioScope, options.reportIndividualSteps());
    }

    private static World buildWorld(Class<?> testClass, List<String> featurePackages, List<String> stepDefinitionPackages) throws IOException {
        return new WorldBuilder(testClass.getClassLoader())
                .withDefaultConverterConfiguration()
                .withFeaturePackages(featurePackages)
                .withStepDefinitionPackages(stepDefinitionPackages)
                .buildWorld();
    }

    @Override
    public void run(RunNotifier notifier) {
        globalScope.begin();
        try {
            super.run(notifier);
        } finally {
            globalScope.end();
        }
    }

    @Override
    protected List<FeatureRunner> getChildren() {
        return delegate.getChildren();
    }

    @Override
    protected Description describeChild(FeatureRunner child) {
        return delegate.describeChild(child);
    }

    @Override
    protected void runChild(FeatureRunner child, RunNotifier notifier) {
        delegate.runChild(child, notifier);
    }

}
