package org.zuchini.reporter;

import com.google.common.base.Optional;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

class ScenarioResult {
    private final Description description;
    private final Optional<Throwable> exception;
    private final boolean assumptionFailed;
    private final boolean ignored;

    public static ScenarioResult failure(Failure failure) {
        return new ScenarioResult(failure.getDescription(), Optional.of(failure.getException()), false, false);
    }

    public static ScenarioResult assumptionFailed(Failure failure) {
        return new ScenarioResult(failure.getDescription(), Optional.of(failure.getException()), true, false);
    }

    public static ScenarioResult ignored(Description description) {
        return new ScenarioResult(description, Optional.<Throwable>absent(), false, true);
    }

    public static ScenarioResult success(Description description) {
        return new ScenarioResult(description, Optional.<Throwable>absent(), false, false);
    }

    private ScenarioResult(Description description, Optional<Throwable> exception, boolean assumptionFailed, boolean ignored) {
        this.description = description;
        this.exception = exception;
        this.assumptionFailed = assumptionFailed;
        this.ignored = ignored;
    }

    public boolean isSuccess() {
        return !(exception.isPresent() || assumptionFailed || ignored);
    }

    public Description getDescription() {
        return description;
    }

    public Optional<Throwable> getException() {
        return exception;
    }

    public boolean isAssumptionFailed() {
        return assumptionFailed;
    }

    public boolean isIgnored() {
        return ignored;
    }
}
