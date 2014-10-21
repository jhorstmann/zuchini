package org.zuchini.runner;

import org.zuchini.model.Feature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorldBuilder {
    private final ClassLoader classLoader;
    private final List<String> featurePackages = new ArrayList<>();
    private final List<String> stepDefinitionPackages  = new ArrayList<>();
    private ConverterConfiguration converterConfiguration = ConverterConfiguration.defaultConfiguration();

    public WorldBuilder(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public WorldBuilder withDefaultConverterConfiguration() {
        this.converterConfiguration = ConverterConfiguration.defaultConfiguration();
        return this;
    }

    public WorldBuilder withConverterConfiguration(ConverterConfiguration converterConfiguration) {
        this.converterConfiguration = converterConfiguration;
        return this;
    }

    public WorldBuilder withFeaturePackages(List<String> featurePackages) {
        this.featurePackages.addAll(featurePackages);
        return this;
    }

    public WorldBuilder addFeaturePackage(String featurePackage) {
        this.featurePackages.add(featurePackage);
        return this;
    }

    public WorldBuilder withStepDefinitionPackages(List<String> stepDefinitionPackages) {
        this.stepDefinitionPackages.addAll(stepDefinitionPackages);
        return this;
    }

    public WorldBuilder addStepDefinitionPackage(String stepDefinitionPackage) {
        this.stepDefinitionPackages.add(stepDefinitionPackage);
        return this;
    }

    public World buildWorld() throws IOException {
        List<Feature> features = FeatureScanner.scan(classLoader, featurePackages);
        List<StepDefinition> stepDefinitions = StepDefinitionScanner.scan(classLoader, stepDefinitionPackages);
        ScopeFactory scopeFactory = ScopeFactoryLoader.load(classLoader);
        Scope globalScope = scopeFactory.createGlobalScope();
        Scope scenarioScope = scopeFactory.createScenarioScope();
        StatementBuilder statementBuilder = new StatementBuilder(converterConfiguration, stepDefinitions);
        List<FeatureStatement> featureStatements = statementBuilder.buildFeatureStatements(features);
        return new World(converterConfiguration, featureStatements, globalScope, scenarioScope);
    }

}
