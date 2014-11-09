package org.zuchini.runner;

public abstract class ScenarioStatement implements Statement {
    public abstract boolean isOutline();
    public abstract void evaluate(Context context) throws Throwable;
}
