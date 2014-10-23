package org.zuchini.runner.tables;

import org.junit.Test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EnhancedBeanInfoTest {
    private Map<String, PropertyDescriptor> getPropertyDescriptorByPropertyName(BeanInfo beanInfo) {
        Map<String, PropertyDescriptor> result = new LinkedHashMap<>();
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            result.put(propertyDescriptor.getName(), propertyDescriptor);
        }
        return result;

    }
    @Test
    public void shouldReadDisplayNameAnnotation() throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(ExampleBean.class);
        Map<String, PropertyDescriptor> properties = getPropertyDescriptorByPropertyName(beanInfo);

        PropertyDescriptor longDescription = properties.get("longDescription");
        assertNotNull(longDescription);
        assertEquals("longDescription", longDescription.getName());
        assertEquals("Long Description Display Name", longDescription.getDisplayName());


    }
}
