package org.zuchini.reporter;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

import javax.annotation.Nullable;

class ScenarioResult {
    private final Description description;
    @Nullable
    private final Throwable exception;
    private final boolean assumptionFailed;
    private final boolean ignored;

    public static ScenarioResult failure(Failure failure) {
        return new ScenarioResult(failure.getDescription(), failure.getException(), false, false);
    }

    public static ScenarioResult assumptionFailed(Failure failure) {
        return new ScenarioResult(failure.getDescription(), failure.getException(), true, false);
    }

    public static ScenarioResult ignored(Description description) {
        return new ScenarioResult(description, null, false, true);
    }

    public static ScenarioResult success(Description description) {
        return new ScenarioResult(description, null, false, false);
    }

    private ScenarioResult(Description description, @Nullable Throwable exception, boolean assumptionFailed, boolean ignored) {
        this.description = description;
        this.exception = exception;
        this.assumptionFailed = assumptionFailed;
        this.ignored = ignored;
    }

    public boolean isSuccess() {
        return !(exception != null || assumptionFailed || ignored);
    }

    public Description getDescription() {
        return description;
    }

    @Nullable
    public Throwable getException() {
        return exception;
    }

    public boolean isAssumptionFailed() {
        return assumptionFailed;
    }

    public boolean isIgnored() {
        return ignored;
    }
}
