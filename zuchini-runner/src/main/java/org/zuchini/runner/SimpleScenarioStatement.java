package org.zuchini.runner;

import org.zuchini.model.Scenario;

import java.util.List;

public class SimpleScenarioStatement extends ScenarioStatement {
    private final Scenario scenario;
    private final List<StepStatement> steps;

    public SimpleScenarioStatement(Scenario scenario, List<StepStatement> steps) {
        this.scenario = scenario;
        this.steps = steps;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public List<StepStatement> getSteps() {
        return steps;
    }

    @Override
    public boolean isOutline() {
        return false;
    }

    @Override
    public void evaluate(ScenarioScope scope) throws Throwable {
        scope.begin();
        try {
            for (StepStatement step : steps) {
                step.evaluate(scope);
            }
        } finally {
            scope.end();
        }
    }
}
