package org.zuchini.runner;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalScope implements Scope {
    private final ThreadLocal<Map<Class<?>, Object>> threadLocalObjects = new ThreadLocal<>();

    @Override
    public void begin() {
        threadLocalObjects.set(new HashMap<Class<?>, Object>());
    }

    @Override
    public <T> T getObject(Class<T> clazz) {
        return Construction.construct(clazz, threadLocalObjects.get());
    }

    @Override
    public void end() {
        threadLocalObjects.remove();
    }
}
