package org.zuchini.runner.tables;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamingConventions {
    public static enum DefaultNamingConventions implements NamingConvention {
        IDENTITY() {
            @Override
            public String toDisplayName(Class<?> beanClass, String property) {
                return property;
            }

            @Override
            public String toProperty(Class<?> beanClass, String displayName) {
                return displayName;
            }
        },
        UPPERCASE_FIRST() {
            @Override
            public String toDisplayName(Class<?> beanClass, String property) {
                return property.substring(0, 1).toUpperCase() + property.substring(1);
            }

            @Override
            public String toProperty(Class<?> beanClass, String displayName) {
                return displayName.substring(0, 1).toLowerCase() + displayName.substring(1);
            }
        },
        TITLECASE() {
            @Override
            public String toDisplayName(Class<?> beanClass, String property) {
                Pattern pattern = Pattern.compile("^[a-z]|(?<=[^A-Z])[A-Z]");
                Matcher matcher = pattern.matcher(property);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    String letter = matcher.group().toUpperCase();
                    matcher.appendReplacement(sb, matcher.start() == 0 ? letter : " " + letter);
                }
                matcher.appendTail(sb);
                return sb.toString();
            }

            @Override
            public String toProperty(Class<?> beanClass, String displayName) {
                Pattern pattern = Pattern.compile("(?:^|\\s+)([A-Z])");
                Matcher matcher = pattern.matcher(displayName);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    String letter = matcher.group(1);
                    matcher.appendReplacement(sb, matcher.start() == 0 ? letter.toLowerCase() : letter);
                }
                matcher.appendTail(sb);
                return sb.toString();
            }
        },
        DISPLAY_NAME() {
            private PropertyDescriptor[] getProperties(Class<?> beanClass) {
                try {
                    BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
                    return beanInfo.getPropertyDescriptors();
                } catch (IntrospectionException e) {
                    throw new IllegalStateException("Could not inspect [" + beanClass.getName() + "]", e);
                }

            }
            @Override
            public String toDisplayName(Class<?> beanClass, String property) {
                for (PropertyDescriptor propertyDescriptor : getProperties(beanClass)) {
                    if (property.equals(propertyDescriptor.getName())) {
                        return propertyDescriptor.getDisplayName();
                    }
                }
                throw new IllegalStateException("Could not find property descriptor for property [" + property + "] in [" + beanClass.getName() + "]");
            }

            @Override
            public String toProperty(Class<?> beanClass, String displayName) {
                for (PropertyDescriptor propertyDescriptor : getProperties(beanClass)) {
                    if (displayName.equals(propertyDescriptor.getDisplayName())) {
                        return propertyDescriptor.getName();
                    }
                }
                throw new IllegalStateException("Could not find property descriptor for display name [" + displayName + "] in [" + beanClass.getName() + "]");
            }
        }
    }
}
