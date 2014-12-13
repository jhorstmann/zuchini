package org.zuchini.runner;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Arrays.asList;

public class Converters {

    static enum DefaultConverters implements Converter {
        BYTE() {
            @Override
            public Object convert(String argument) {
                return Byte.valueOf(argument);
            }
        },
        SHORT() {
            @Override
            public Object convert(String argument) {
                return Short.valueOf(argument);
            }
        },
        INT() {
            @Override
            public Object convert(String argument) {
                return Integer.valueOf(argument);
            }
        },
        LONG() {
            @Override
            public Object convert(String argument) {
                return Long.valueOf(argument);
            }
        },
        FLOAT() {
            @Override
            public Object convert(String argument) {
                return Float.valueOf(argument);
            }
        },
        DOUBLE() {
            @Override
            public Object convert(String argument) {
                return Double.valueOf(argument);
            }
        },
        CHARACTER() {
            @Override
            public Object convert(String argument) {
                if (argument.length() != 1) {
                    throw new IllegalArgumentException("Could not convert string of length " + argument.length() + " to char");
                } else {
                    return argument.charAt(0);
                }
            }
        },
        BOOLEAN() {
            private final Map<String, Boolean> MAP = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            {
                for (String value : asList("true", "1", "yes", "y")) {
                    MAP.put(value, Boolean.TRUE);
                }
                for (String value : asList("false", "0", "no", "n")) {
                    MAP.put(value, Boolean.FALSE);
                }
            }

            @Override
            public Object convert(String argument) {
                Boolean b = MAP.get(argument);
                if (b == null) {
                    throw new IllegalArgumentException("Could not convert argument to boolean");
                } else {
                    return b;
                }
            }
        },
        STRING() {
            @Override
            public Object convert(String argument) {
                return argument;
            }
        },
        BIGINT() {
            @Override
            public Object convert(String argument) {
                return new BigInteger(argument);
            }
        },
        BIGDECIMAL() {
            @Override
            public Object convert(String argument) {
                return new BigDecimal(argument);
            }
        };
    }

    static final Map<Class<?>, Converter<?>> DEFAULT_CONVERTERS;

    static {
        Map<Class<?>, Converter<?>> map = new HashMap<>(32);
        map.put(Byte.TYPE, DefaultConverters.BYTE);
        map.put(Byte.class, DefaultConverters.BYTE);
        map.put(Short.TYPE, DefaultConverters.SHORT);
        map.put(Short.class, DefaultConverters.SHORT);
        map.put(Integer.TYPE, DefaultConverters.INT);
        map.put(Integer.class, DefaultConverters.INT);
        map.put(Long.TYPE, DefaultConverters.LONG);
        map.put(Long.class, DefaultConverters.LONG);
        map.put(Float.TYPE, DefaultConverters.FLOAT);
        map.put(Float.class, DefaultConverters.FLOAT);
        map.put(Double.TYPE, DefaultConverters.DOUBLE);
        map.put(Double.class, DefaultConverters.DOUBLE);
        map.put(Character.TYPE, DefaultConverters.CHARACTER);
        map.put(Character.class, DefaultConverters.CHARACTER);
        map.put(Boolean.TYPE, DefaultConverters.BOOLEAN);
        map.put(Boolean.class, DefaultConverters.BOOLEAN);
        map.put(String.class, DefaultConverters.STRING);
        map.put(Object.class, DefaultConverters.STRING);
        map.put(BigInteger.class, DefaultConverters.BIGINT);
        map.put(BigDecimal.class, DefaultConverters.BIGDECIMAL);

        DEFAULT_CONVERTERS = Collections.unmodifiableMap(map);
    }

    private Converters() {

    }

    public static <T> Converter<T> getConverter(Scope scope, Class<T> parameterType, Annotation[] parameterAnnotations) {
        for (Annotation parameterAnnotation : parameterAnnotations) {
            if (parameterAnnotation.annotationType() == Convert.class) {
                final Class<Converter<?>> argumentConverterClass = ((Convert) parameterAnnotation).value();
                final Converter<?> converter = scope.getObject(argumentConverterClass);
                return cast(parameterType, converter);
            }
        }

        final Converter<?> converter = DEFAULT_CONVERTERS.get(parameterType);
        if (converter != null) {
            return cast(parameterType, converter);
        } else if (parameterType.isEnum()) {
            return newEnumConverter(parameterType);
        } else {
            throw new IllegalStateException("Could not find argument converter for type [" + parameterType.getName() + "]");
        }
    }

    @SuppressWarnings("unchecked")
    static <T> Converter<T> newEnumConverter(Class<?> parameterType) {
        final Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) parameterType;
        return (Converter<T>) new EnumConverter(enumType);
    }

    @SuppressWarnings("unchecked")
    private static <T> Converter<T> cast(Class<T> parameterType, Converter<?> converter) {
        // TODO: Check generic type parameter of converter?
        return (Converter<T>) converter;
    }
}
