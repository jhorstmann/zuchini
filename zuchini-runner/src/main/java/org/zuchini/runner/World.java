package org.zuchini.runner;

import java.util.List;

public class World implements Context {
    private final Scope globalScope;
    private final Scope scenarioScope;
    private final ConverterConfiguration converterConfiguration;
    private final List<FeatureStatement> featureStatements;

    World(Scope globalScope, Scope scenarioScope, ConverterConfiguration converterConfiguration, List<FeatureStatement> featureStatements) {
        this.globalScope = globalScope;
        this.scenarioScope = scenarioScope;
        this.converterConfiguration = converterConfiguration;
        this.featureStatements = featureStatements;
    }

    @Override
    public Scope getGlobalScope() {
        return globalScope;
    }

    @Override
    public Scope getScenarioScope() {
        return scenarioScope;
    }

    public ConverterConfiguration getConverterConfiguration() {
        return converterConfiguration;
    }

    public List<FeatureStatement> getFeatureStatements() {
        return featureStatements;
    }


}
