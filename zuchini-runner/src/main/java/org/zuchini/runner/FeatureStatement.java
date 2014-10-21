package org.zuchini.runner;

import org.zuchini.model.Feature;

import java.util.Collections;
import java.util.List;

public class FeatureStatement implements Statement {
    private final Feature feature;
    private final List<? extends ScenarioStatement> scenarios;

    public FeatureStatement(Feature feature, List<? extends ScenarioStatement> scenarios) {
        this.feature = feature;
        this.scenarios = scenarios;
    }

    public Feature getFeature() {
        return feature;
    }

    public List<? extends ScenarioStatement> getScenarios() {
        return Collections.unmodifiableList(scenarios);
    }

    @Override
    public void evaluate(Scope scope) throws Throwable {
        for (ScenarioStatement scenario : scenarios) {
            scenario.evaluate(scope);
        }
    }

}
