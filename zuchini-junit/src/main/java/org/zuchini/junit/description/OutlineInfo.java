package org.zuchini.junit.description;

public @interface OutlineInfo {
    String uri();
    int lineNumber();
    String keyword();
    String description();
    String[] tags();
    String[] comments();

}
