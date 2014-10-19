package org.zuchini.junit.description;

public @interface OutlineInfo {
    int lineNumber();
    String keyword();
    String description();
    String[] tags();
    String[] comments();

}
