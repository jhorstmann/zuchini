package org.zuchini.runner;

public interface Statement {
    void evaluate(Scope scope) throws Throwable;
}
