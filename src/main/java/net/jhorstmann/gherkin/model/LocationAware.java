package net.jhorstmann.gherkin.model;

public interface LocationAware {
    String getUri();
    int getLineNumber();
}
