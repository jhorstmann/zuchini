package org.zuchini.runner;

import org.zuchini.model.Background;
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
import java.util.Set;
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
    private final List<HookDefinition> hookDefinitions;
    private final Map<String, Closure> methodCache;

    StatementBuilder(List<StepDefinition> stepDefinitions, List<HookDefinition> hookDefinitions) {
        this.stepDefinitions = stepDefinitions;
        this.hookDefinitions = hookDefinitions;
        this.methodCache = new HashMap<>();
    }

    List<FeatureStatement> buildFeatureStatements(List<Feature> features) {
        final List<FeatureStatement> statements = new ArrayList<>(features.size());
        for (Feature feature : features) {
            statements.add(buildFeatureStatement(feature));
        }
        return statements;
    }

    private FeatureStatement buildFeatureStatement(Feature feature) {
        final List<StepContainer> scenarios = feature.getScenarios();
        final List<ScenarioStatement> statements = new ArrayList<>(scenarios.size());
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
        final List<Scenario> scenarios = outline.buildScenarios();
        final List<SimpleScenarioStatement> statements = new ArrayList<>(scenarios.size());
        for (Scenario scenario : scenarios) {
            statements.add(buildScenarioStatement(scenario));
        }

        return new OutlineStatement(outline, statements);
    }

    private SimpleScenarioStatement buildScenarioStatement(Scenario scenario) {
        final List<Step> steps = new ArrayList<>();

        for (Background background : scenario.getBackground()) {
            steps.addAll(background.getSteps());
        }
        steps.addAll(scenario.getSteps());

        final List<HookStatement> beforeHooks = new ArrayList<>();
        final List<HookStatement> afterHooks = new ArrayList<>();

        outer:
        for (HookDefinition hookDefinition : hookDefinitions) {
            final List<HookStatement> list = hookDefinition.getEvent() == HookDefinition.Event.BEFORE ? beforeHooks : afterHooks;

            final Set<String> tags = hookDefinition.getTags();
            for (String tag : tags) {
                if (!scenario.getTags().contains(tag)) {
                    continue outer;
                }
            }

            list.add(new HookStatement(hookDefinition.getMethod()));
        }

        final BeforeScenarioStatement beforeScenarioStatement = new BeforeScenarioStatement(beforeHooks);
        final AfterScenarioStatement afterScenarioStatement = new AfterScenarioStatement(afterHooks);

        final List<StepStatement> stepStatements = new ArrayList<>(steps.size());

        if (!steps.isEmpty()) {
            final Step firstStep = steps.get(0);
            final Step lastStep = steps.get(steps.size() - 1);

            for (Step step : steps) {
                final Statement beforeStep = step == firstStep ? beforeScenarioStatement : null;
                final Statement afterStep = step == lastStep ? afterScenarioStatement : null;
                final Statement exceptional = afterScenarioStatement;
                final Closure closure = findClosure(step);
                final StepStatement stepStatement = new StepStatement(step, closure.getMethod(), closure.getArguments(), beforeStep, afterStep, exceptional);
                stepStatements.add(stepStatement);
            }
        }

        return new SimpleScenarioStatement(scenario, stepStatements);
    }

    private Closure findClosure(Step step) {
        Closure closure = methodCache.get(step.getName());
        if (closure == null) {
            closure = findMethod(step.getName());
            methodCache.put(step.getName(), closure);
        }
        return closure;
    }

    private String[] groups(Matcher matcher) {
        final int groupCount = matcher.groupCount();
        final String[] groups = new String[groupCount];
        for (int i = 0; i < groupCount; i++) {
            groups[i] = matcher.group(i+1);
        }
        return groups;
    }

    private Closure findMethod(String description) {
        final List<Closure> methods = new ArrayList<>(2);
        for (StepDefinition stepDefinition : stepDefinitions) {
            final Pattern pattern = stepDefinition.getPattern();
            final Matcher matcher = pattern.matcher(description);
            if (matcher.matches()) {
                final Method method = stepDefinition.getMethod();
                final Closure closure = new Closure(method, groups(matcher));
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
