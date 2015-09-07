package gherkin.ast;

import com.fasterxml.jackson.annotation.JsonCreator;

public class DocStringNode extends ArgumentNode {
    private final String contentType;
    private final String content;

    @JsonCreator
    public DocStringNode(Location location, String contentType, String content) {
        super(location);
        this.contentType = contentType;
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
