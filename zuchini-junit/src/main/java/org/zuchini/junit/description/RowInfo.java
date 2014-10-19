package org.zuchini.junit.description;

public @interface RowInfo {
    int lineNumber();
    String[] tags();
    String[] comments();
    String[] cells();

}
