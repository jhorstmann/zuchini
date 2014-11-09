package org.zuchini.runner;

public interface Statement {
    void evaluate(Context context) throws Throwable;
}
