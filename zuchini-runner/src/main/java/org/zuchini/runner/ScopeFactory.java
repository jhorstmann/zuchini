package org.zuchini.runner;

public interface ScopeFactory {
    Scope createGlobalScope();
    Scope createScenarioScope();
}
