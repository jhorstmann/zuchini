package org.zuchini.runner.tables;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public abstract class EnhancedBeanInfo implements BeanInfo {
    private final BeanInfo delegate;
    private final PropertyDescriptor[] propertyDescriptors;

    protected EnhancedBeanInfo(Class<?> beanClass) {
        try {
            this.delegate = Introspector.getBeanInfo(beanClass, Introspector.IGNORE_IMMEDIATE_BEANINFO);
            this.propertyDescriptors = getPropertyDescriptorsWithDisplayName(delegate);

        } catch (IntrospectionException e) {
            throw new IllegalStateException("Could not introspect [" + beanClass.getName() + "]");
        }
    }

    private static PropertyDescriptor[] getPropertyDescriptorsWithDisplayName(BeanInfo beanInfo) {
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            updateDisplayName(propertyDescriptor);
        }
        return propertyDescriptors;

    }

    private static void updateDisplayName(PropertyDescriptor propertyDescriptor) {
        Method readMethod = propertyDescriptor.getReadMethod();
        Method writeMethod = propertyDescriptor.getWriteMethod();
        DisplayName displayName = null;
        if (readMethod != null) {
            displayName = readMethod.getAnnotation(DisplayName.class);
        }
        if (displayName == null && writeMethod != null) {
            displayName = writeMethod.getAnnotation(DisplayName.class);
        }
        if (displayName != null && displayName.value() != null) {
            propertyDescriptor.setDisplayName(displayName.value());
        }
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return propertyDescriptors;
    }

    @Override
    public BeanDescriptor getBeanDescriptor() {
        return delegate.getBeanDescriptor();
    }

    @Override
    public int getDefaultPropertyIndex() {
        return delegate.getDefaultPropertyIndex();
    }

    @Override
    public EventSetDescriptor[] getEventSetDescriptors() {
        return delegate.getEventSetDescriptors();
    }

    @Override
    public int getDefaultEventIndex() {
        return delegate.getDefaultEventIndex();
    }

    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        return delegate.getMethodDescriptors();
    }

    @Override
    public BeanInfo[] getAdditionalBeanInfo() {
        return delegate.getAdditionalBeanInfo();
    }

    @Override
    public java.awt.Image getIcon(int iconKind) {
        return delegate.getIcon(iconKind);
    }

}
