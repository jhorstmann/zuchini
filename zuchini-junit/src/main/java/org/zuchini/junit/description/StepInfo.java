package org.zuchini.junit.description;

public @interface StepInfo {
    String uri();
    int lineNumber();
    String keyword();
    String description();
    String[] tags();
    String[] comments();
    RowInfo[] rows();
    String[] docs();
}
