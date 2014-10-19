package org.zuchini.runner.cukes;

import org.junit.Test;
import org.zuchini.runner.ConverterConfiguration;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.ScenarioScope;
import org.zuchini.runner.ThreadLocalScope;
import org.zuchini.runner.WorldBuilder;

import java.util.List;

public class CukesTest {
    @Test
    public void shouldExecuteScenario() throws Throwable {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        List<FeatureStatement> statements = new WorldBuilder(cl)
                .withConverterConfiguration(ConverterConfiguration.defaultConfiguration())
                .addFeaturePackage("features/cukes")
                .addStepDefinitionPackage("org.zuchini.runner.cukes")
                .buildFeatureStatements();

        ScenarioScope scope = new ThreadLocalScope();
        for (FeatureStatement statement : statements) {
            statement.evaluate(scope);
        }

    }
}
