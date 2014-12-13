package org.zuchini.runner.tables;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesNamingConvention implements NamingConvention {
    private final NamingConvention delegate;
    private final Map<String, String> propertyToHeader;
    private final Map<String, String> headerToProperty;

    public PropertiesNamingConvention(NamingConvention delegate, Class<?> beanClass) {
        this.delegate = delegate;

        Properties properties = loadProperty(beanClass);
        if (properties.isEmpty()) {
            propertyToHeader = Collections.emptyMap();
            headerToProperty = Collections.emptyMap();
        } else {
            propertyToHeader = new HashMap<>(properties.size());
            headerToProperty = new HashMap<>(properties.size());
            for (String property : properties.stringPropertyNames()) {
                String header  = properties.getProperty(property);
                propertyToHeader.put(property, header);
                headerToProperty.put(header, property);
            }
        }
    }

    private static Properties loadProperty(Class<?> beanClass) {
        Properties properties = new Properties();
        String name = beanClass.getSimpleName() + ".properties";
        URL resource = beanClass.getResource(name);
        if (resource != null) {
            try (InputStream in = resource.openStream()) {
                properties.load(in);
            } catch (IOException e) {
                throw new IllegalStateException("Could not load resource [" + name + "]");
            }
        }
        return properties;
    }

    @Override
    public String toDisplayName(String property) {
        String displayName = propertyToHeader.get(property);
        return displayName != null ? displayName : delegate.toDisplayName(property);
    }

    @Override
    public String toProperty(String displayName) {
        String property = headerToProperty.get(displayName);
        return property != null ? property : delegate.toProperty(displayName);
    }
}
