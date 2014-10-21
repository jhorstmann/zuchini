package org.zuchini.runner;

import java.util.List;

public class World {
    private final ConverterConfiguration converterConfiguration;
    private final List<FeatureStatement> featureStatements;
    private final Scope globalScope;
    private final Scope scenarioScope;

    World(ConverterConfiguration converterConfiguration, List<FeatureStatement> featureStatements, Scope globalScope, Scope scenarioScope) {
        this.converterConfiguration = converterConfiguration;
        this.featureStatements = featureStatements;
        this.globalScope = globalScope;
        this.scenarioScope = scenarioScope;
    }

    public ConverterConfiguration getConverterConfiguration() {
        return converterConfiguration;
    }

    public List<FeatureStatement> getFeatureStatements() {
        return featureStatements;
    }

    public Scope getGlobalScope() {
        return globalScope;
    }

    public Scope getScenarioScope() {
        return scenarioScope;
    }


}
