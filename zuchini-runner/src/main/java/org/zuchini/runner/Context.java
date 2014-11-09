package org.zuchini.runner;

public interface Context {
    Scope getGlobalScope();
    Scope getScenarioScope();
    ConverterConfiguration getConverterConfiguration();
}
