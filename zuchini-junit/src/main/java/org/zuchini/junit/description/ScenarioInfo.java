package org.zuchini.junit.description;

public @interface ScenarioInfo {
    int lineNumber();
    String keyword();
    String description();
    String[] tags();
    String[] comments();

}
