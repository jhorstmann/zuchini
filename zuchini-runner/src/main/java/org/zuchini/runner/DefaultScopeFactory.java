package org.zuchini.runner;

public class DefaultScopeFactory implements ScopeFactory {
    @Override
    public Scope createGlobalScope() {
        return new GlobalScope();
    }

    @Override
    public Scope createScenarioScope() {
        return new ThreadLocalScope();
    }
}
