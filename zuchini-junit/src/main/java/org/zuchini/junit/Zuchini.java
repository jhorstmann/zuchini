package org.zuchini.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.ScenarioScope;
import org.zuchini.runner.WorldBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class Zuchini extends ParentRunner<FeatureRunner> {

    private final List<FeatureRunner> children;

    public Zuchini(Class<?> testClass) throws InitializationError, IOException, IllegalAccessException, InstantiationException {
        super(testClass);
        this.children = buildChildren(testClass);
    }

    private List<FeatureRunner> buildChildren(Class<?> testClass) throws InstantiationException, IllegalAccessException, IOException, InitializationError {
        ZuchiniOptions options = testClass.getAnnotation(ZuchiniOptions.class);
        ScenarioScope scope = options.scope().newInstance();

        List<FeatureStatement> features = new WorldBuilder(testClass.getClassLoader())
                .withDefaultConverterConfiguration()
                .withFeaturePackages(asList(options.featurePackages()))
                .withStepDefinitionPackages(asList(options.stepDefinitionPackages()))
                .buildFeatureStatements();

        ArrayList<FeatureRunner> children = new ArrayList<>(features.size());
        for (FeatureStatement feature : features) {
            children.add(new FeatureRunner(testClass, scope, feature));
        }

        return children;
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
