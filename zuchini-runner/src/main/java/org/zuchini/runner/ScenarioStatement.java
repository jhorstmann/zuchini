package org.zuchini.runner;

public abstract class ScenarioStatement implements Statement {
    public abstract boolean isOutline();
    public abstract void evaluate(ScenarioScope scope) throws Throwable;
}
