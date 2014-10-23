package org.zuchini.runner.tables;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

public class BeanInfoNamingConvention implements NamingConvention {
    private final Class<?> beanClass;
    private final PropertyDescriptor[] properties;

    public BeanInfoNamingConvention(Class<?> beanClass) {
        this.beanClass = beanClass;
        this.properties = getProperties(beanClass);
    }

    private static PropertyDescriptor[] getProperties(Class<?> beanClass) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            return beanInfo.getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new IllegalStateException("Could not inspect [" + beanClass.getName() + "]", e);
        }
    }

    @Override
    public String toDisplayName(String property) {
        for (PropertyDescriptor propertyDescriptor : properties) {
            if (property.equals(propertyDescriptor.getName())) {
                return propertyDescriptor.getDisplayName();
            }
        }
        throw new IllegalStateException(
                "Could not find property descriptor for property [" + property + "] in [" + beanClass.getName() + "]");
    }

    @Override
    public String toProperty(String displayName) {
        for (PropertyDescriptor propertyDescriptor : properties) {
            if (displayName.equals(propertyDescriptor.getDisplayName())) {
                return propertyDescriptor.getName();
            }
        }
        throw new IllegalStateException(
                "Could not find property descriptor for display name [" + displayName + "] in [" + beanClass.getName() + "]");
    }

    @Override
    public String toString() {
        return "BeanInfoNamingConvention<" + beanClass.getName() + ">";
    }
}
