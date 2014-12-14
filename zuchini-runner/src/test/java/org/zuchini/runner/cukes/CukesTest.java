package org.zuchini.runner.cukes;

import org.junit.Test;
import org.zuchini.runner.World;
import org.zuchini.runner.WorldBuilder;

public class CukesTest {
    @Test
    public void shouldExecuteScenario() throws Throwable {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        World world = new WorldBuilder(cl)
                .addFeaturePackage("features/cukes")
                .addStepDefinitionPackage("org.zuchini.runner.cukes")
                .buildWorld();

        world.run();
    }
}
