package org.zuchini.runner.tables;

public interface NamingConvention {

    String toDisplayName(Class<?> beanClass, String property);

    String toProperty(Class<?> beanClass, String displayName);
}
