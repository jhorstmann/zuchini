package org.zuchini.junit.description;

public @interface FeatureInfo {
    String uri();
    int lineNumber();
    String keyword();
    String name();

    String[] tags();
    String[] comments();


    ScenarioInfo[] scenarios();

}
