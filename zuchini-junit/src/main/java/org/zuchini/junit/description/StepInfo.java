package org.zuchini.junit.description;

public @interface StepInfo {
    int lineNumber();
    String keyword();
    String description();
    String[] tags();
    String[] comments();
    RowInfo[] rows();
}
