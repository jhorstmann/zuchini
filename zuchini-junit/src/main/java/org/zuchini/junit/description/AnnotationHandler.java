package org.zuchini.junit.description;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
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
            return annotationHashCode(proxy);
        } else if (args.length == 0 && methodName.equals("toString")) {
            return annotationToString(proxy);
        } else if (args.length == 1 && methodName.equals("equals") && method.getParameterTypes()[0] == Object.class) {
            Object arg = args[0];
            if (arg == null) {
                return false;
            } else if (proxy == arg) {
                return true;
            } else {
                return annotationEquals(proxy, arg);
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
                    throw new IllegalStateException(
                            "Unsupported mapping from [" + beanReturnType.getName() + "] to [" + annotationReturnType.getName() + "]");
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
            throw new IllegalStateException(
                    "Unsupported method [" + methodName + "] called on annotation [" + bean.getClass().getName() + "]");
        }
    }

    public boolean annotationEquals(Object proxy, Object other) {
        if (!(other instanceof Annotation)) {
            return false;
        }
        Annotation that = (Annotation) other;
        if (!this.annotationType.equals(that.annotationType())) {
            return false;
        }
        for (Method method : annotationType.getDeclaredMethods()) {
            final Object thisValue, thatValue;
            try {
                thisValue = invoke(proxy, method, EMPTY_ARGS);
                thatValue = method.invoke(other);
            } catch (RuntimeException ex1) {
                throw ex1;
            } catch (Throwable throwable1) {
                throw new RuntimeException(throwable1);
            }
            if (thisValue instanceof byte[] && thatValue instanceof byte[]) {
                if (!Arrays.equals((byte[]) thisValue, (byte[]) thatValue)) return false;
            } else if (thisValue instanceof short[] && thatValue instanceof short[]) {
                if (!Arrays.equals((short[]) thisValue, (short[]) thatValue)) return false;
            } else if (thisValue instanceof int[] && thatValue instanceof int[]) {
                if (!Arrays.equals((int[]) thisValue, (int[]) thatValue)) return false;
            } else if (thisValue instanceof long[] && thatValue instanceof long[]) {
                if (!Arrays.equals((long[]) thisValue, (long[]) thatValue)) return false;
            } else if (thisValue instanceof float[] && thatValue instanceof float[]) {
                if (!Arrays.equals((float[]) thisValue, (float[]) thatValue)) return false;
            } else if (thisValue instanceof double[] && thatValue instanceof double[]) {
                if (!Arrays.equals((double[]) thisValue, (double[]) thatValue)) return false;
            } else if (thisValue instanceof char[] && thatValue instanceof char[]) {
                if (!Arrays.equals((char[]) thisValue, (char[]) thatValue)) return false;
            } else if (thisValue instanceof boolean[] && thatValue instanceof boolean[]) {
                if (!Arrays.equals((boolean[]) thisValue, (boolean[]) thatValue)) return false;
            } else if (thisValue instanceof Object[] && thatValue instanceof Object[]) {
                if (!Arrays.equals((Object[]) thisValue, (Object[]) thatValue)) return false;
            } else {
                if (!thisValue.equals(thatValue)) return false;
            }
        }
        return true;
    }

    public int annotationHashCode(Object proxy) {
        int hashCode = 0;
        for (Method method : annotationType.getDeclaredMethods()) {
            final Object value;
            try {
                value = invoke(proxy, method, EMPTY_ARGS);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
            final int memberValueHashCode;
            if (value instanceof boolean[]) {
                memberValueHashCode = Arrays.hashCode((boolean[]) value);
            } else if (value instanceof short[]) {
                memberValueHashCode = Arrays.hashCode((short[]) value);
            } else if (value instanceof int[]) {
                memberValueHashCode = Arrays.hashCode((int[]) value);
            } else if (value instanceof long[]) {
                memberValueHashCode = Arrays.hashCode((long[]) value);
            } else if (value instanceof float[]) {
                memberValueHashCode = Arrays.hashCode((float[]) value);
            } else if (value instanceof double[]) {
                memberValueHashCode = Arrays.hashCode((double[]) value);
            } else if (value instanceof byte[]) {
                memberValueHashCode = Arrays.hashCode((byte[]) value);
            } else if (value instanceof char[]) {
                memberValueHashCode = Arrays.hashCode((char[]) value);
            } else if (value instanceof Object[]) {
                memberValueHashCode = Arrays.hashCode((Object[]) value);
            } else {
                memberValueHashCode = value.hashCode();
            }
            hashCode = 31 * hashCode + memberValueHashCode;
        }
        return hashCode;
    }

    public String annotationToString(Object proxy) {
        StringBuilder string = new StringBuilder();
        string.append('@').append(annotationType.getName()).append('[');
        Method[] methods = annotationType.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            string.append(methods[i].getName()).append('=');
            final Object value;
            try {
                value = invoke(proxy, methods[i], EMPTY_ARGS);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
            if (value instanceof boolean[]) {
                string.append(Arrays.toString((boolean[]) value));
            } else if (value instanceof byte[]) {
                string.append(Arrays.toString((byte[]) value));
            } else if (value instanceof short[]) {
                string.append(Arrays.toString((short[]) value));
            } else if (value instanceof int[]) {
                string.append(Arrays.toString((int[]) value));
            } else if (value instanceof long[]) {
                string.append(Arrays.toString((long[]) value));
            } else if (value instanceof float[]) {
                string.append(Arrays.toString((float[]) value));
            } else if (value instanceof double[]) {
                string.append(Arrays.toString((double[]) value));
            } else if (value instanceof char[]) {
                string.append(Arrays.toString((char[]) value));
            } else if (value instanceof Object[]) {
                string.append(Arrays.toString((Object[]) value));
            } else {
                string.append(value);
            }
            if (i < methods.length - 1) {
                string.append(", ");
            }
        }
        return string.append(']').toString();
    }
}
