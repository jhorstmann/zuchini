package org.zuchini.runner.cukes;

import org.junit.Test;
import org.zuchini.runner.DefaultConverterConfiguration;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.Scope;
import org.zuchini.runner.World;
import org.zuchini.runner.WorldBuilder;

public class CukesTest {
    @Test
    public void shouldExecuteScenario() throws Throwable {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        World world = new WorldBuilder(cl)
                .withConverterConfiguration(DefaultConverterConfiguration.defaultConfiguration())
                .addFeaturePackage("features/cukes")
                .addStepDefinitionPackage("org.zuchini.runner.cukes")
                .buildWorld();

        Scope globalScope = world.getGlobalScope();
        globalScope.begin();
        try {
            for (FeatureStatement statement : world.getFeatureStatements()) {
                statement.evaluate(world);
            }
        } finally {
            globalScope.end();
        }
    }
}
