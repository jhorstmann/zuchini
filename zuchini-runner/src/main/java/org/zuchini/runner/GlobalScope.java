package org.zuchini.runner;

import java.util.HashMap;
import java.util.Map;

public class GlobalScope implements Scope {
    private final Map<Class<?>, Object> objects = new HashMap<>();
    @Override
    public void begin() {
        if (!objects.isEmpty()) {
            throw new IllegalStateException("Objects map should be empty when beginning a global scope");
        }
    }

    @Override
    public <T> T getObject(Class<T> clazz) {
        return Construction.construct(clazz, objects);
    }

    @Override
    public void end() {
        objects.clear();
    }
}
