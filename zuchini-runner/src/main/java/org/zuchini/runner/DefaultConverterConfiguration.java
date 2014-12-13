package org.zuchini.runner;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class DefaultConverterConfiguration implements ConverterConfiguration {

    private static final DefaultConverterConfiguration DEFAULT_CONFIGURATION = new DefaultConverterConfiguration();

    public static DefaultConverterConfiguration defaultConfiguration() {
        return DEFAULT_CONFIGURATION;
    }

    public static DefaultConverterConfiguration withDefaults(Map<Class<?>, Converter<?>> additionalConverters) {
        Map<Class<?>, Converter<?>> defaultConverters = DefaultConverters.getDefaultConverters();
        Map<Class<?>, Converter<?>> converters = new HashMap<>(defaultConverters.size() + additionalConverters.size());
        converters.putAll(defaultConverters);
        converters.putAll(converters);
        return new DefaultConverterConfiguration(converters);
    }

    private final Map<Class<?>, Converter<?>> converters;

    private DefaultConverterConfiguration() {
        this.converters = DefaultConverters.getDefaultConverters();
    }

    private DefaultConverterConfiguration(Map<Class<?>, Converter<?>> converters) {
        this.converters = converters;
    }

    @Override
    public <T> Converter<T> getConverter(Scope scope, Class<T> parameterType, Annotation[] parameterAnnotations) {
        for (Annotation parameterAnnotation : parameterAnnotations) {
            if (parameterAnnotation.annotationType() == Convert.class) {
                Class<Converter<?>> argumentConverterClass = ((Convert) parameterAnnotation).value();
                return cast(parameterType, scope.getObject(argumentConverterClass));
            }
        }
        Converter<?> converter = converters.get(parameterType);
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
        Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) parameterType;
        return (Converter<T>) new EnumConverter(enumType);
    }

    @SuppressWarnings("unchecked")
    private static <T> Converter<T> cast(Class<T> parameterType, Converter<?> converter) {
        // TODO: Check generic type parameter of converter?
        return (Converter<T>) converter;
    }
}
