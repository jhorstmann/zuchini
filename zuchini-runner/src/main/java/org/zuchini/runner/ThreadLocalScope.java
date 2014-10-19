package org.zuchini.runner;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalScope implements ScenarioScope {
    private final ThreadLocal<Map<Class<?>, Object>> threadLocalObjects = new ThreadLocal<>();

    @Override
    public void begin() {
        threadLocalObjects.set(new HashMap<Class<?>, Object>());
    }

    @Override
    public <T> T getObject(Class<T> clazz) {
        Map<Class<?>, Object> objects = threadLocalObjects.get();
        Object obj = objects.get(clazz);
        if (obj == null) {
            try {
                obj = clazz.newInstance();
                objects.put(clazz, obj);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Could not create instance of [" + clazz.getName() + "]");
            }
        }
        return clazz.cast(obj);
    }

    @Override
    public void end() {
        threadLocalObjects.remove();
    }
}
