package org.zuchini.junit.description;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static java.util.Arrays.asList;

public final class AnnotationHandler implements InvocationHandler {
    private static final Object[] EMPTY_ARGS = {};
    private static final Set<Class<?>> ATOMIC_TYPES = new HashSet<Class<?>>(asList(
            Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE,
            Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class,
            String.class));
    private final Class<? extends Annotation> annotationType;
    private final Object bean;

    private AnnotationHandler(Class<? extends Annotation> annotationType, Object bean) {
        this.annotationType = annotationType;
        this.bean = bean;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T create(Class<T> annotationType, Object bean) {
        return (T) Proxy.newProxyInstance(annotationType.getClassLoader(), new Class<?>[]{annotationType},
                new AnnotationHandler(annotationType, bean));
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Annotation> castToAnnotationType(Class<?> type) {
        return (Class<? extends Annotation>) type;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args == null) {
            args = EMPTY_ARGS;
        }

        String methodName = method.getName();
        if (args.length == 0 && methodName.equals("hashCode")) {
            return hashCode();
        } else if (args.length == 0 && methodName.equals("toString")) {
            return toString();
        } else if (args.length == 1 && methodName.equals("equals") && method.getParameterTypes()[0] == Object.class) {
            Object arg = args[0];
            if (arg == null) {
                return false;
            } else if (proxy == arg) {
                return true;
            } else {
                return getClass() == proxy.getClass() && this == Proxy.getInvocationHandler(arg);
            }
        } else if (args.length == 0 && methodName.equals("annotationType")) {
            return annotationType;
        } else if (args.length == 0) {
            String getter = "get" + methodName.substring(0, 1).toUpperCase(Locale.ROOT) + methodName.substring(1);
            Method beanMethod = bean.getClass().getMethod(getter);
            Class<?> beanReturnType = beanMethod.getReturnType();
            Class<?> annotationReturnType = method.getReturnType();
            if (annotationReturnType.isArray()) {
                Class<?> componentType = annotationReturnType.getComponentType();
                if (beanReturnType.isArray()) {
                    Object array = beanMethod.invoke(bean);
                    int length = Array.getLength(array);
                    Object result = Array.newInstance(componentType, length);
                    if (ATOMIC_TYPES.contains(componentType)) {
                        for (int i = 0; i < length; i++) {
                            Array.set(result, i, Array.get(array, i));
                        }
                        return result;
                    } else if (componentType.isAnnotation()) {
                        for (int i = 0; i < length; i++) {
                            Array.set(result, i, create(castToAnnotationType(componentType), Array.get(array, i)));
                        }
                        return result;
                    } else {
                        throw new IllegalStateException("Unsupported component type [" + componentType.getName() + "]");
                    }
                } else if (Collection.class.isAssignableFrom(beanReturnType)) {
                    Collection<?> collection = (Collection<?>) beanMethod.invoke(bean);
                    Object result = Array.newInstance(componentType, collection.size());
                    if (ATOMIC_TYPES.contains(componentType)) {
                        int i = 0;
                        for (Object obj : collection) {
                            Array.set(result, i++, obj);
                        }
                        return result;
                    } else if (componentType.isAnnotation()) {
                        int i = 0;
                        for (Object obj : collection) {
                            Array.set(result, i++, create(castToAnnotationType(componentType), obj));
                        }
                        return result;
                    } else {
                        throw new IllegalStateException("Unsupported component type [" + componentType.getName() + "]");
                    }
                } else {
                    throw new IllegalStateException("Unsupported mapping from [" + beanReturnType.getName() + "] to [" + annotationReturnType.getName() + "]");
                }
            } else if (ATOMIC_TYPES.contains(annotationReturnType)) {
                return beanMethod.invoke(bean);
            } else if (annotationReturnType.isAnnotation()) {
                Object obj = beanMethod.invoke(bean);
                return create(castToAnnotationType(annotationReturnType), obj);
            } else {
                throw new IllegalStateException("Unsupported return type [" + annotationReturnType.getName() + "]");
            }
        } else {
            throw new IllegalStateException("Unsupported method [" + methodName + "] called on annotation [" + bean.getClass().getName() + "]");
        }
    }
}
