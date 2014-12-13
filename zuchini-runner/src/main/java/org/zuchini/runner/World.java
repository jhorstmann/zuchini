package org.zuchini.runner;

import java.util.List;

public class World implements Context {
    private final Scope globalScope;
    private final Scope scenarioScope;
    private final List<FeatureStatement> featureStatements;

    World(Scope globalScope, Scope scenarioScope, List<FeatureStatement> featureStatements) {
        this.globalScope = globalScope;
        this.scenarioScope = scenarioScope;
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

    public List<FeatureStatement> getFeatureStatements() {
        return featureStatements;
    }


}
