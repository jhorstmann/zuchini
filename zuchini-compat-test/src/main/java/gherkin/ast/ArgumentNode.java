package gherkin.ast;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonSubTypes({ @JsonSubTypes.Type(value = DocStringNode.class , name = "DocString"), @JsonSubTypes.Type(value = DataTableNode.class, name = "DataTable")})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,  property = "type")
public abstract class ArgumentNode extends Node {
    protected ArgumentNode(Location location) {
        super(location);
    }
}
