package org.zuchini.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.Scope;
import org.zuchini.runner.World;
import org.zuchini.runner.WorldBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class ZuchiniRunnerDelegate {
    private final List<FeatureRunner> children;

    public ZuchiniRunnerDelegate(Class<?> testClass, ZuchiniOptions options, Scope scenarioScope) throws InitializationError, IOException {
        World world = buildWorld(testClass, options.featurePackages(), options.stepDefinitionPackages());
        this.children = buildChildren(testClass, world.getFeatureStatements(), scenarioScope, options.reportIndividualSteps());
    }

    private static List<FeatureRunner> buildChildren(Class<?> testClass, List<FeatureStatement> featureStatements, Scope scenarioScope, boolean reportIndividualSteps) throws InitializationError {

        ArrayList<FeatureRunner> children = new ArrayList<>(featureStatements.size());
        for (FeatureStatement featureStatement : featureStatements) {
            children.add(new FeatureRunner(testClass, scenarioScope, featureStatement, reportIndividualSteps));
        }

        return children;
    }

    private static World buildWorld(Class<?> testClass, String[] featurePackages, String[] stepDefinitionPackages) throws IOException {
        return new WorldBuilder(testClass.getClassLoader())
                .withDefaultConverterConfiguration()
                .withFeaturePackages(asList(featurePackages))
                .withStepDefinitionPackages(asList(stepDefinitionPackages))
                .buildWorld();
    }

    public List<FeatureRunner> getChildren() {
        return children;
    }

    public Description describeChild(FeatureRunner child) {
        return child.getDescription();
    }

    public void runChild(FeatureRunner child, RunNotifier notifier) {
        child.run(notifier);
    }
}
