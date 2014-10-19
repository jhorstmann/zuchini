package org.zuchini.runner;

import org.zuchini.model.Feature;

import java.util.List;

public class FeatureStatement implements Statement {
    private final Feature feature;
    private final List<? extends ScenarioStatement> scenarios;

    public FeatureStatement(Feature feature, List<? extends ScenarioStatement> scenarios) {
        this.feature = feature;
        this.scenarios = scenarios;
    }

    @Override
    public void evaluate(ScenarioScope scope) throws Throwable {
        for (ScenarioStatement scenario : scenarios) {
            scenario.evaluate(scope);
        }
    }

}
