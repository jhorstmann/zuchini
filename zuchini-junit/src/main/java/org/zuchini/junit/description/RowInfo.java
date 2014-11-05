package org.zuchini.junit.description;

public @interface RowInfo {
    String uri();
    int lineNumber();
    String[] tags();
    String[] comments();
    String[] cells();

}
