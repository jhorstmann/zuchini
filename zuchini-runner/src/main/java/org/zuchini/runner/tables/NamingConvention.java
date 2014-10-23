package org.zuchini.runner.tables;

public interface NamingConvention {

    String toDisplayName(String property);

    String toProperty(String displayName);
}
