package org.zuchini.runner.cukes;

import org.junit.Test;
import org.zuchini.runner.ConverterConfiguration;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.World;
import org.zuchini.runner.WorldBuilder;

public class CukesTest {
    @Test
    public void shouldExecuteScenario() throws Throwable {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        World world = new WorldBuilder(cl)
                .withConverterConfiguration(ConverterConfiguration.defaultConfiguration())
                .addFeaturePackage("features/cukes")
                .addStepDefinitionPackage("org.zuchini.runner.cukes")
                .buildWorld();

        world.getGlobalScope().begin();
        for (FeatureStatement statement : world.getFeatureStatements()) {
            statement.evaluate(world.getScenarioScope());
        }
        world.getGlobalScope().end();

    }
}
