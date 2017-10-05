package org.zuchini.runner;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Arrays.asList;

class Converters {

    enum DefaultConverters implements Converter<Object> {
        BYTE_PRIMITIVE {
            @Override
            public Object convert(String argument) {
                return Byte.valueOf(argument);
            }
        },
        BYTE_WRAPPER {
            @Override
            public Object convert(String argument) {
                return argument == null ? null : BYTE_PRIMITIVE.convert(argument);
            }
        },
        SHORT_PRIMITIVE {
            @Override
            public Object convert(String argument) {
                return Short.valueOf(argument);
            }
        },
        SHORT_WRAPPER {
            @Override
            public Object convert(String argument) {
                return argument == null ? null : SHORT_PRIMITIVE.convert(argument);
            }
        },
        INT_PRIMITIVE() {
            @Override
            public Object convert(String argument) {
                return Integer.valueOf(argument);
            }
        },
        INT_WRAPPER() {
            @Override
            public Object convert(String argument) {
                return argument == null ? null : INT_PRIMITIVE.convert(argument);
            }
        },
        LONG_PRIMITIVE {
            @Override
            public Object convert(String argument) {
                return Long.valueOf(argument);
            }
        },
        LONG_WRAPPER {
            @Override
            public Object convert(String argument) {
                return argument == null ? null : LONG_PRIMITIVE.convert(argument);
            }
        },
        FLOAT_PRIMITIVE {
            @Override
            public Object convert(String argument) {
                return Float.valueOf(argument);
            }
        },
        FLOAT_WRAPPER {
            @Override
            public Object convert(String argument) {
                return argument == null ? null : FLOAT_PRIMITIVE.convert(argument);
            }
        },
        DOUBLE_PRIMITIVE {
            @Override
            public Object convert(String argument) {
                return Double.valueOf(argument);
            }
        },
        DOUBLE_WRAPPER {
            @Override
            public Object convert(String argument) {
                return argument == null ? null : DOUBLE_PRIMITIVE.convert(argument);
            }
        },
        CHARACTER_PRIMITIVE {
            @Override
            public Object convert(String argument) {
                if (argument.length() != 1) {
                    throw new IllegalArgumentException("Could not convert string of length " + argument.length() + " to char");
                } else {
                    return argument.charAt(0);
                }
            }
        },
        CHARACTER_WRAPPER {
            @Override
            public Object convert(String argument) {
                return argument == null ? null : CHARACTER_PRIMITIVE.convert(argument);
            }
        },
        BOOLEAN_PRIMITIVE {
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
        BOOLEAN_WRAPPER {
            @Override
            public Object convert(String argument) {
                return argument == null ? null : BOOLEAN_PRIMITIVE.convert(argument);
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

    static class EnumConverter<E extends Enum<E>> implements Converter<E> {

        private final Class<E> enumClass;

        EnumConverter(Class<E> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public E convert(@Nullable String argument) {
            if (argument == null || argument.length() == 0) {
                return null;
            } else {
                return Enum.valueOf(enumClass, argument);
            }
        }

    }

    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];

    private static final Map<Class<?>, Converter<?>> DEFAULT_CONVERTERS;

    static {
        Map<Class<?>, Converter<?>> map = new HashMap<>(32);
        map.put(Byte.TYPE, DefaultConverters.BYTE_PRIMITIVE);
        map.put(Byte.class, DefaultConverters.BYTE_WRAPPER);
        map.put(Short.TYPE, DefaultConverters.SHORT_PRIMITIVE);
        map.put(Short.class, DefaultConverters.SHORT_WRAPPER);
        map.put(Integer.TYPE, DefaultConverters.INT_PRIMITIVE);
        map.put(Integer.class, DefaultConverters.INT_WRAPPER);
        map.put(Long.TYPE, DefaultConverters.LONG_PRIMITIVE);
        map.put(Long.class, DefaultConverters.LONG_WRAPPER);
        map.put(Float.TYPE, DefaultConverters.FLOAT_PRIMITIVE);
        map.put(Float.class, DefaultConverters.FLOAT_WRAPPER);
        map.put(Double.TYPE, DefaultConverters.DOUBLE_PRIMITIVE);
        map.put(Double.class, DefaultConverters.DOUBLE_WRAPPER);
        map.put(Character.TYPE, DefaultConverters.CHARACTER_PRIMITIVE);
        map.put(Character.class, DefaultConverters.CHARACTER_WRAPPER);
        map.put(Boolean.TYPE, DefaultConverters.BOOLEAN_PRIMITIVE);
        map.put(Boolean.class, DefaultConverters.BOOLEAN_WRAPPER);
        map.put(String.class, DefaultConverters.STRING);
        map.put(Object.class, DefaultConverters.STRING);
        map.put(BigInteger.class, DefaultConverters.BIGINT);
        map.put(BigDecimal.class, DefaultConverters.BIGDECIMAL);

        DEFAULT_CONVERTERS = Collections.unmodifiableMap(map);
    }

    private Converters() {

    }

    static <T> Converter<T> getConverter(Scope scope, Class<T> parameterType) {
        return getConverter(scope, parameterType, EMPTY_ANNOTATIONS);
    }

    static <T> Converter<T> getConverter(Scope scope, Class<T> parameterType, Annotation[] parameterAnnotations) {
        for (Annotation parameterAnnotation : parameterAnnotations) {
            if (parameterAnnotation.annotationType() == Convert.class) {
                final Class<? extends Converter<?>> argumentConverterClass = ((Convert) parameterAnnotation).value();
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
