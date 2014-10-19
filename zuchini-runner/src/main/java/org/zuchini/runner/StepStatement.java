package org.zuchini.runner;


import org.zuchini.model.Step;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StepStatement implements Statement {
    private final Step step;
    private final Method method;
    private final Object[] args;

    public StepStatement(Step step, Method method, Object[] args) {
        this.step = step;
        this.method = method;
        this.args = args;
    }

    public Step getStep() {
        return step;
    }

    @Override
    public void evaluate(ScenarioScope scope) throws Throwable {
        try {
            Object target = scope.getObject(method.getDeclaringClass());
            method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }
}
