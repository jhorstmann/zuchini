package org.zuchini.runner;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

class HookDefinition {
    public enum Event {
        BEFORE, AFTER
    }

    private final Event event;
    private final Set<String> tags;
    private final Method method;

    HookDefinition(Event event, Set<String> tags, Method method) {
        this.event = event;
        this.tags = tags;
        this.method = method;
    }

    Event getEvent() {
        return event;
    }

    Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    Method getMethod() {
        return method;
    }

}
