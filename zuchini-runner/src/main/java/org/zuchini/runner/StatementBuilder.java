package org.zuchini.runner;

import org.zuchini.model.Feature;
import org.zuchini.model.Outline;
import org.zuchini.model.Scenario;
import org.zuchini.model.Step;
import org.zuchini.model.StepContainer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StatementBuilder {
    static class Closure {
        private final Method method;
        private final String[] arguments;

        Closure(Method method, String[] arguments) {
            this.method = method;
            this.arguments = arguments;
        }

        Method getMethod() {
            return method;
        }

        String[] getArguments() {
            return arguments;
        }
    }
    private final List<StepDefinition> stepDefinitions;
    private final Map<String, Closure> methodCache;

    StatementBuilder(List<StepDefinition> stepDefinitions) {
        this.stepDefinitions = stepDefinitions;
        this.methodCache = new HashMap<>();
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
        Closure closure = methodCache.get(step.getDescription());
        if (closure == null) {
            closure = findMethod(step.getDescription());
            methodCache.put(step.getDescription(), closure);
        }
        return new StepStatement(step, closure.getMethod(), closure.getArguments());
    }

    private String[] groups(Matcher matcher) {
        int groupCount = matcher.groupCount();
        String[] groups = new String[groupCount];
        for (int i = 0; i < groupCount; i++) {
            groups[i] = matcher.group(i+1);
        }
        return groups;
    }

    private Closure findMethod(String description) {
        List<Closure> methods = new ArrayList<>(2);
        for (StepDefinition stepDefinition : stepDefinitions) {
            Pattern pattern = stepDefinition.getPattern();
            Matcher matcher = pattern.matcher(description);
            if (matcher.matches()) {
                Method method = stepDefinition.getMethod();
                Closure closure = new Closure(method, groups(matcher));
                methods.add(closure);
            }
        }
        if (methods.isEmpty()) {
            throw new IllegalStateException("Could not find step definition for [" + description + "]");
        } else if (methods.size() > 1) {
            throw new IllegalStateException("Multiple matching methods for [" + description + "]");
        } else {
            return methods.get(0);
        }
    }
}
