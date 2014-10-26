package org.zuchini.spring;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.HashMap;
import java.util.Map;

class SpringThreadLocalScope implements Scope {
    static class ThreadLocalStringObjectMap extends ThreadLocal<Map<String, Object>> {
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<>();
        }
    }
    private final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocalStringObjectMap();

    public void clear() {
        threadLocal.get().clear();
    }

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Map<String, Object> objects = threadLocal.get();
        if (!objects.containsKey(name)) {
            Object object = objectFactory.getObject();
            objects.put(name, object);
            return object;
        } else {
            return objects.get(name);
        }
    }

    @Override
    public Object remove(String name) {
        return threadLocal.get().remove(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }

}
