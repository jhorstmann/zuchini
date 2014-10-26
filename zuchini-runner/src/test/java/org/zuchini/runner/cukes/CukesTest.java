package org.zuchini.runner.cukes;

import org.junit.Test;
import org.zuchini.runner.ConverterConfiguration;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.GlobalScope;
import org.zuchini.runner.Scope;
import org.zuchini.runner.ThreadLocalScope;
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

        Scope globalScope = new GlobalScope();
        Scope scenarioScope = new ThreadLocalScope();

        globalScope.begin();
        try {
            for (FeatureStatement statement : world.getFeatureStatements()) {
                statement.evaluate(scenarioScope);
            }
        } finally {
            globalScope.end();
        }
    }
}
