package org.zuchini.runner;

import java.lang.annotation.Annotation;

public interface ConverterConfiguration {
    <T> Converter<T> getConverter(Scope scope, Class<T> parameterType, Annotation[] parameterAnnotations);
}
