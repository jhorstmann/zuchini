package org.zuchini.runner;

import org.zuchini.model.Feature;
import org.zuchini.model.Outline;
import org.zuchini.model.Row;
import org.zuchini.model.Scenario;
import org.zuchini.model.Step;
import org.zuchini.model.StepContainer;
import org.zuchini.runner.tables.Datatable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StatementBuilder {
    private final ConverterConfiguration converterConfiguration;
    private final List<StepDefinition> stepDefinitions;
    private final Map<String, StepStatement> stepStatements;

    StatementBuilder(ConverterConfiguration converterConfiguration, List<StepDefinition> stepDefinitions) {
        this.converterConfiguration = converterConfiguration;
        this.stepDefinitions = stepDefinitions;
        this.stepStatements = new HashMap<>();
    }

    public List<FeatureStatement> buildFeatureStatements(List<Feature> features) {
        List<FeatureStatement> statements = new ArrayList<>(features.size());
        for (Feature feature : features) {
            statements.add(buildFeatureStatement(feature));
        }
        return statements;
    }

    public FeatureStatement buildFeatureStatement(Feature feature) {
        List<StepContainer> scenarios = feature.getScenarios();
        List<ScenarioStatement> statements = new ArrayList<>(scenarios.size());
        for (StepContainer stepContainer : scenarios) {
            if (stepContainer instanceof Outline) {
                Outline outline = (Outline) stepContainer;
                statements.add(buildOutlineStatement(outline));
            } else {
                statements.add(buildScenarioStatement((Scenario) stepContainer));
            }
        }
        return new FeatureStatement(feature, statements);
    }

    private OutlineStatement buildOutlineStatement(Outline outline) {
        List<Scenario> scenarios = outline.buildScenarios();
        List<SimpleScenarioStatement> statements = new ArrayList<>(scenarios.size());
        for (Scenario scenario : scenarios) {
            statements.add(buildScenarioStatement(scenario));
        }

        return new OutlineStatement(outline, statements);
    }

    private SimpleScenarioStatement buildScenarioStatement(Scenario scenario) {
        List<Step> steps = scenario.getStepsIncludingBackground();
        List<StepStatement> statements = new ArrayList<>(steps.size());
        for (Step step : steps) {
            statements.add(buildStepStatement(step));
        }
        return new SimpleScenarioStatement(scenario, statements);
    }

    private StepStatement buildStepStatement(Step step) {
        StepStatement stepStatement = stepStatements.get(step.getDescription());
        if (stepStatement == null) {
            stepStatement = buildStepStatementImpl(step);
            stepStatements.put(step.getDescription(), stepStatement);
        }
        return stepStatement;
    }

    private Object[] convertArguments(Matcher matcher, Class<?>[] parameterTypes, Annotation[][] parameterAnnotations) {
        assert (parameterTypes.length == parameterAnnotations.length);
        assert (matcher.groupCount() <= parameterTypes.length);
        Object[] result = new Object[matcher.groupCount()];
        for (int i = 0, len = matcher.groupCount(); i < len; i++) {
            Converter<?> converter = converterConfiguration.getConverter(parameterTypes[i], parameterAnnotations[i]);
            Object argument = converter.convert(matcher.group(i + 1));
            result[i] = argument;
        }
        return result;
    }

    private StepStatement buildStepStatementImpl(Step step) {
        String description = step.getDescription();
        for (StepDefinition stepDefinition : stepDefinitions) {
            Pattern pattern = stepDefinition.getPattern();
            Matcher matcher = pattern.matcher(description);
            if (matcher.matches()) {
                Method method = stepDefinition.getMethod();
                Class<?>[] parameterTypes = method.getParameterTypes();
                Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                int groupCount = matcher.groupCount();
                int parameterCount = parameterTypes.length;
                if (groupCount == parameterCount) {
                    Object[] args = convertArguments(matcher, parameterTypes, parameterAnnotations);
                    return new StepStatement(step, method, args);
                } else {
                    List<Row> rows = step.getRows();
                    List<String> docs = step.getDocs();
                    if (!rows.isEmpty() && groupCount +1 == parameterCount && parameterTypes[groupCount] == Datatable.class) {
                        Object[] args = convertArguments(matcher, parameterTypes, parameterAnnotations);
                        args[groupCount] = new Datatable(rows);
                        return new StepStatement(step, method, args);
                    } else if(docs.size() == 1 && groupCount + 1 == parameterCount && parameterTypes[groupCount] == String.class) {
                        Object[] args = convertArguments(matcher, parameterTypes, parameterAnnotations);
                        args[groupCount] = docs.get(0);
                        return new StepStatement(step, method, args);
                    } else {
                        String methodName = method.getDeclaringClass().getName() + "." + method.getName();
                        throw new IllegalStateException("Could not convert parameters for step [" + description + "] to method [" + methodName + "]");
                    }
                }
            }
        }
        throw new IllegalStateException("Could not find step definition for [" + step.getDescription() + "]");
    }


}
