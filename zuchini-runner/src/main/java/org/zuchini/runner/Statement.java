package org.zuchini.runner;

public interface Statement {
    void evaluate(ScenarioScope scope) throws Throwable;
}
