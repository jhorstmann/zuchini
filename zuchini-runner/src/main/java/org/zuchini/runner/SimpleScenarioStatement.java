package org.zuchini.runner;

import org.zuchini.model.Scenario;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class SimpleScenarioStatement extends ScenarioStatement {
    private final Scenario scenario;
    private final List<StepStatement> steps;
    private final List<HookStatement> beforeHooks;
    private final List<HookStatement> afterHooks;

    public SimpleScenarioStatement(Scenario scenario, List<StepStatement> steps, List<HookStatement> beforeHooks, List<HookStatement> afterHooks) {
        this.scenario = scenario;
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
