package org.zuchini.runner;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Arrays.asList;

public class ConverterConfiguration {
    enum DefaultConverters implements Converter {
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
        }
    }

    private static final Map<Class<?>, Converter<?>> DEFAULT_CONVERTERS = new HashMap<>();

    static {
        DEFAULT_CONVERTERS.put(Byte.TYPE, DefaultConverters.BYTE);
        DEFAULT_CONVERTERS.put(Byte.class, DefaultConverters.BYTE);
        DEFAULT_CONVERTERS.put(Short.TYPE, DefaultConverters.SHORT);
        DEFAULT_CONVERTERS.put(Short.class, DefaultConverters.SHORT);
        DEFAULT_CONVERTERS.put(Integer.TYPE, DefaultConverters.INT);
        DEFAULT_CONVERTERS.put(Integer.class, DefaultConverters.INT);
        DEFAULT_CONVERTERS.put(Long.TYPE, DefaultConverters.LONG);
        DEFAULT_CONVERTERS.put(Long.class, DefaultConverters.LONG);
        DEFAULT_CONVERTERS.put(Float.TYPE, DefaultConverters.FLOAT);
        DEFAULT_CONVERTERS.put(Float.class, DefaultConverters.FLOAT);
        DEFAULT_CONVERTERS.put(Double.TYPE, DefaultConverters.DOUBLE);
        DEFAULT_CONVERTERS.put(Double.class, DefaultConverters.DOUBLE);
        DEFAULT_CONVERTERS.put(Character.TYPE, DefaultConverters.CHARACTER);
        DEFAULT_CONVERTERS.put(Character.class, DefaultConverters.CHARACTER);
        DEFAULT_CONVERTERS.put(Boolean.TYPE, DefaultConverters.BOOLEAN);
        DEFAULT_CONVERTERS.put(Boolean.class, DefaultConverters.BOOLEAN);
        DEFAULT_CONVERTERS.put(String.class, DefaultConverters.STRING);
        DEFAULT_CONVERTERS.put(Object.class, DefaultConverters.STRING);
        DEFAULT_CONVERTERS.put(BigInteger.class, DefaultConverters.BIGINT);
        DEFAULT_CONVERTERS.put(BigDecimal.class, DefaultConverters.BIGDECIMAL);
    }

    private static final ConverterConfiguration DEFAULT_CONFIGURATION = new ConverterConfiguration();

    public static ConverterConfiguration defaultConfiguration() {
        return DEFAULT_CONFIGURATION;
    }

    public static ConverterConfiguration withDefaults(Map<Class<?>, Converter<?>> additionalConverters) {
        Map<Class<?>, Converter<?>> converters = new HashMap<>(DEFAULT_CONVERTERS.size() + additionalConverters.size());
        converters.putAll(DEFAULT_CONVERTERS);
        converters.putAll(converters);
        return new ConverterConfiguration(converters);
    }

    private final Map<Class<?>, Converter<?>> converters;

    private ConverterConfiguration() {
        this.converters = DEFAULT_CONVERTERS;
    }

    private ConverterConfiguration(Map<Class<?>, Converter<?>> converters) {
        this.converters = converters;
    }

    public <T> Converter<T> getConverter(Class<T> parameterType, Annotation[] parameterAnnotations) {
        for (Annotation parameterAnnotation : parameterAnnotations) {
            if (parameterAnnotation.annotationType() == Convert.class) {
                Class<Converter<?>> argumentConverterClass = ((Convert) parameterAnnotation).value();
                // TODO: use global scope to lookup converters
                return cast(parameterType, Construction.construct(argumentConverterClass));
            }
        }
        Converter<?> converter = converters.get(parameterType);
        if (converter != null) {
            return cast(parameterType, converter);
        } else {
            throw new IllegalStateException("Could not find argument converter for type [" + parameterType.getName() + "]");
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Converter<T> cast(Class<T> parameterType, Converter<?> converter) {
        // TODO: Check generic type parameter of converter?
        return (Converter<T>) converter;
    }
}
