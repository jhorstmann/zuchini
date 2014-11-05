package org.zuchini.junit.description;

public @interface ScenarioInfo {
    String uri();
    int lineNumber();
    String keyword();
    String description();
    String[] tags();
    String[] comments();

}
