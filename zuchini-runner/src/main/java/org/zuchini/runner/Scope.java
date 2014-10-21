package org.zuchini.runner;

public interface Scope {
    void begin();
    <T> T getObject(Class<T> clazz);
    void end();
}
