package org.zuchini.runner;


import org.zuchini.model.Row;
import org.zuchini.model.Step;
import org.zuchini.runner.tables.Datatable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class StepStatement implements Statement {
    private final Step step;
    private final Method method;
    private final String[] stringArguments;

    public StepStatement(Step step, Method method, String[] stringArguments) {
        this.step = step;
        this.method = method;
        this.stringArguments = stringArguments;
    }

    public Step getStep() {
        return step;
    }

    private Object[] convertArguments(Context context, Class<?>[] parameterTypes, Annotation[][] parameterAnnotations) {
        assert (parameterTypes.length == parameterAnnotations.length);
        assert (stringArguments.length <= parameterTypes.length);
        Scope globalScope = context.getGlobalScope();
        Object[] typedArguments = new Object[stringArguments.length];
        for (int i = 0; i < stringArguments.length; i++) {
            Converter<?> converter = Converters.getConverter(globalScope, parameterTypes[i], parameterAnnotations[i]);
            Object argument = converter.convert(stringArguments[i]);
            typedArguments[i] = argument;
        }
        return typedArguments;
    }

    private Object[] convertArguments(Context context) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        int parameterCount = parameterTypes.length;
        if (stringArguments.length == parameterCount) {
            Object[] typedArguments = convertArguments(context, parameterTypes, parameterAnnotations);
            return typedArguments;
        } else {
            List<Row> rows = step.getRows();
            List<String> docs = step.getDocs();
            if (!rows.isEmpty() && stringArguments.length + 1 == parameterCount && parameterTypes[stringArguments.length] == Datatable.class) {
                Object[] typedArguments = convertArguments(context, parameterTypes, parameterAnnotations);
                typedArguments[stringArguments.length] = Datatable.fromRows(rows);
                return typedArguments;
            } else if(docs.size() == 1 && stringArguments.length + 1 == parameterCount && parameterTypes[stringArguments.length] == String.class) {
                Object[] typedArguments = convertArguments(context, parameterTypes, parameterAnnotations);
                typedArguments[stringArguments.length] = docs.get(0);
                return typedArguments;
            } else {
                String description = step.getDescription();
                String methodName = method.getDeclaringClass().getName() + "." + method.getName();
                throw new IllegalStateException("Could not convert parameters " + Arrays.toString(stringArguments) + " for step [" + description + "] to method [" + methodName + "] with arguments " + Arrays.toString(parameterTypes));
            }
        }
    }

    @Override
    public void evaluate(Context context) throws Throwable {
        try {
            Scope scenarioScope = context.getScenarioScope();
            Object target = scenarioScope.getObject(method.getDeclaringClass());
            Object[] typedArguments = convertArguments(context);
            method.invoke(target, typedArguments);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }
}
