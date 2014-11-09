package org.zuchini.runner;

import org.zuchini.model.Feature;
import org.zuchini.parser.FeatureParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorldBuilder {
    private final ClassLoader classLoader;
    private final List<String> featurePackages = new ArrayList<>();
    private final List<File> featureFiles = new ArrayList<>();
    private final List<String> stepDefinitionPackages  = new ArrayList<>();
    private Scope globalScope;
    private Scope scenarioScope;
    private ConverterConfiguration converterConfiguration;

    public WorldBuilder(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public WorldBuilder withDefaultConverterConfiguration() {
        this.converterConfiguration = DefaultConverterConfiguration.defaultConfiguration();
        return this;
    }

    public WorldBuilder withConverterConfiguration(ConverterConfiguration converterConfiguration) {
        this.converterConfiguration = converterConfiguration;
        return this;
    }

    public WorldBuilder withGlobalScope(Scope globalScope) {
        this.globalScope = globalScope;
        return this;
    }

    public WorldBuilder withScenarioScope(Scope scenarioScope) {
        this.scenarioScope = scenarioScope;
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

    public WorldBuilder withFeatureFiles(List<File> featureFiles) {
        this.featureFiles.addAll(featureFiles);
        return this;
    }

    public WorldBuilder addFeatureFile(File featureFile) {
        this.featureFiles.add(featureFile);
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
        List<Feature> features = new ArrayList<>();
        if (!featurePackages.isEmpty()) {
            features.addAll(FeatureScanner.scan(classLoader, featurePackages));
        }
        if (!featureFiles.isEmpty()) {
            for (File file : featureFiles) {
                features.add(FeatureParser.getFeature(file));
            }
        }

        if (converterConfiguration == null) {
            converterConfiguration = DefaultConverterConfiguration.defaultConfiguration();
        }
        if (globalScope == null) {
            globalScope = new GlobalScope();
        }
        if (scenarioScope == null) {
            scenarioScope = new ThreadLocalScope();
        }

        List<StepDefinition> stepDefinitions = StepDefinitionScanner.scan(classLoader, stepDefinitionPackages);
        StatementBuilder statementBuilder = new StatementBuilder(stepDefinitions);
        List<FeatureStatement> featureStatements = statementBuilder.buildFeatureStatements(features);

        return new World(globalScope, scenarioScope, converterConfiguration, featureStatements);
    }

}
