package org.zuchini.junit5;

import org.zuchini.runner.Context;
import org.zuchini.runner.Scope;

class ContextImpl implements Context {
    private final Scope globalScope;
    private final Scope scenarioScope;

    ContextImpl(Scope globalScope, Scope scenarioScope) {
        this.globalScope = globalScope;
        this.scenarioScope = scenarioScope;
    }

    @Override
    public Scope getGlobalScope() {
        return globalScope;
    }

    @Override
    public Scope getScenarioScope() {
        return scenarioScope;
    }
}
