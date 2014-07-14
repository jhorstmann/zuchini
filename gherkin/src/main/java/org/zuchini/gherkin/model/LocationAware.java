package org.zuchini.gherkin.model;

public interface LocationAware {
    String getUri();
    int getLineNumber();
}
