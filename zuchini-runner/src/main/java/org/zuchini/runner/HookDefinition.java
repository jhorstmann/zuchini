package org.zuchini.runner;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

public class HookDefinition {
    public enum Event {
        BEFORE, AFTER
    }

    private final Event event;
    private final Set<String> tags;
    private final Method method;

    public HookDefinition(Event event, Set<String> tags, Method method) {
        this.event = event;
        this.tags = tags;
        this.method = method;
    }

    public Event getEvent() {
        return event;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public Method getMethod() {
        return method;
    }

}
