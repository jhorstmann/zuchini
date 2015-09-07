package org.zuchini.junit.description;

public @interface ScenarioInfo {
    String uri();
    int lineNumber();
    String keyword();
    String name();
    String[] tags();
    String[] comments();
    StepInfo[] steps();

}
