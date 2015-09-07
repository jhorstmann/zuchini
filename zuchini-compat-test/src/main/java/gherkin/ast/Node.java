package gherkin.ast;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

public abstract class Node {
    private final String type = getClass().getSimpleName();
    private final Location location;

    protected Node(Location location) {
        this.location = location;
    }

    @JsonIgnore
    // TODO: We do not have exact line numbers on tags and comments
    public Location getLocation() {
        return location;
    }
}
