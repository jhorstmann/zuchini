package org.zuchini.junit.description;

public @interface StepInfo {
    String uri();
    int lineNumber();
    String keyword();
    String name();
    String[] tags();
    String[] comments();
    RowInfo[] rows();
    String[] docs();
}
