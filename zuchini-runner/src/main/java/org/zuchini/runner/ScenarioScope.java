package org.zuchini.runner;

public interface ScenarioScope {
    void begin();
    <T> T getObject(Class<T> clazz);
    void end();
}
