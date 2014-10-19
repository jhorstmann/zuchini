package org.zuchini.runner;

public interface Converter<T> {
    T convert(String argument);


}
