package org.zuchini.runner;

import org.zuchini.model.Outline;

import java.util.List;

public class OutlineStatement extends ScenarioStatement {
    private final Outline outline;
    private final List<SimpleScenarioStatement> scenarios;

    public OutlineStatement(Outline outline, List<SimpleScenarioStatement> scenarios) {
        this.outline = outline;
        this.scenarios = scenarios;
    }

    @Override
    public boolean isOutline() {
        return true;
    }

    @Override
    public void evaluate(ScenarioScope scope) throws Throwable {
        for (SimpleScenarioStatement scenario : scenarios) {
            scenario.evaluate(scope);
        }

    }
}
