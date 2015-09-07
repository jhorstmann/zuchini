package org.zuchini.junit.description;

public @interface OutlineInfo {
    String uri();
    int lineNumber();
    String keyword();
    String name();
    String[] tags();
    String[] comments();

}
