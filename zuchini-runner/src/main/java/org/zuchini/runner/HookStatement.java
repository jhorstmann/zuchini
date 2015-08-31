package org.zuchini.runner;

import org.zuchini.model.Scenario;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HookStatement implements Statement {
    private final Scenario scenario;
    private final Method method;

    public HookStatement(Scenario scenario, Method method) {
        this.scenario = scenario;
        this.method = method;
    }

    @Override
    public void evaluate(Context context) throws Throwable {
        try {
            final Scope scenarioScope = context.getScenarioScope();
            final Object target = scenarioScope.getObject(method.getDeclaringClass());
            method.invoke(target);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }
}
