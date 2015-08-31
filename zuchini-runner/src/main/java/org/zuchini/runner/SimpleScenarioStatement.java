package org.zuchini.runner;

import org.zuchini.model.Scenario;

import javax.annotation.Nullable;
import java.util.List;

public class SimpleScenarioStatement extends ScenarioStatement {
    private final Scenario scenario;
    @Nullable
    private final BackgroundStatement background;
    private final List<StepStatement> steps;
    private final List<HookStatement> beforeHooks;
    private final List<HookStatement> afterHooks;

    public SimpleScenarioStatement(Scenario scenario, BackgroundStatement background, List<StepStatement> steps, List<HookStatement> beforeHooks, List<HookStatement> afterHooks) {
        this.scenario = scenario;
        this.background = background;
        this.steps = steps;
        this.beforeHooks = beforeHooks;
        this.afterHooks = afterHooks;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public List<StepStatement> getSteps() {
        return steps;
    }

    public List<HookStatement> getBeforeHooks() {
        return beforeHooks;
    }

    public List<HookStatement> getAfterHooks() {
        return afterHooks;
    }

    @Override
    public boolean isOutline() {
        return false;
    }

    @Override
    public void evaluate(Context context) throws Throwable {
        Scope scope = context.getScenarioScope();
        scope.begin();
        try {
            for (HookStatement beforeHook : beforeHooks) {
                beforeHook.evaluate(context);
            }

            if (background != null) {
                background.evaluate(context);
            }

            for (StepStatement step : steps) {
                step.evaluate(context);
            }

            for (HookStatement afterHook : afterHooks) {
                afterHook.evaluate(context);
            }
        } finally {
            scope.end();
        }
    }
}
