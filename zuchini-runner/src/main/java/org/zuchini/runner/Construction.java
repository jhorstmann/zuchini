package org.zuchini.runner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

class Construction {

    private Construction() {
    }

    static <T> T construct(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Could not create instance of [" + clazz.getName() + "]", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Could not create instance of [" + clazz.getName() + "], constructor threw", e.getCause());
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Can not instantiate a class without default constructor");
        }
    }

    static <T> T construct(Class<T> clazz, Map<Class<?>, Object> cache) {
        T object = clazz.cast(cache.get(clazz));
        if (object == null) {
            object = construct(clazz);
            cache.put(clazz, object);
        }
        return object;
    }
}
