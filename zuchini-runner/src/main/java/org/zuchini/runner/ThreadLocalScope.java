package org.zuchini.runner;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalScope implements Scope {
    private final ThreadLocal<Map<Class<?>, Object>> threadLocalObjects = new ThreadLocal<>();

    @Override
    public void begin() {
        if (threadLocalObjects.get() != null) {
            throw new IllegalStateException("Scope is already initialized");
        }
        threadLocalObjects.set(new HashMap<Class<?>, Object>());
    }

    @Override
    public <T> T getObject(Class<T> clazz) {
        Map<Class<?>, Object> objects = threadLocalObjects.get();
        if (objects == null) {
            throw new IllegalStateException("Scope is not initialized");
        }
        return Construction.construct(clazz, objects);
    }

    @Override
    public void end() {
        threadLocalObjects.remove();
    }
}
