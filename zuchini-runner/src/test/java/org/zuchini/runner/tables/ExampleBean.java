package org.zuchini.runner.tables;

import org.zuchini.runner.tables.DisplayName;

public class ExampleBean {
    private int width;
    private int height;
    private String longDescription;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @DisplayName("Long Description")
    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
}
