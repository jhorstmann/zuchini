package org.zuchini.junit5;

import org.junit.platform.engine.*;
import org.zuchini.runner.GlobalScope;
import org.zuchini.runner.ThreadLocalScope;
import org.zuchini.runner.World;
import org.zuchini.runner.WorldBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;

import static java.util.Arrays.asList;

public class ZuchiniTestEngine implements TestEngine {
    @Override
    public String getId() {
        return getClass().getName();
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest engineDiscoveryRequest, UniqueId uniqueId) {
        ConfigurationParameters config = engineDiscoveryRequest.getConfigurationParameters();
        ContextImpl context = new ContextImpl(new GlobalScope(), new ThreadLocalScope());
        World world = buildWorld(context, config);

        return ZuchiniTestDescriptor.fromFeatures(uniqueId, context, getId(), world.getFeatureStatements());
    }

    private World buildWorld(ContextImpl context, ConfigurationParameters config) {
        String featurePackages = config.get("zuchini.featurePackages").orElse("");
        String stepDefinitionPackages = config.get("zuchini.stepDefinitionPackages").orElse("");
        // TODO: Support reportIndividualSteps
        boolean reportIndividualSteps = config.getBoolean("zuchini.reportIndividualSteps").orElse(false);

        try {
            return new WorldBuilder(Thread.currentThread().getContextClassLoader())
                    .withFeaturePackages(asList(featurePackages))
                    .withStepDefinitionPackages(asList(stepDefinitionPackages))
                    .withGlobalScope(context.getGlobalScope())
                    .withScenarioScope(context.getScenarioScope())
                    .buildWorld();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public void execute(ExecutionRequest executionRequest) {
        ConfigurationParameters config = executionRequest.getConfigurationParameters();
        boolean reportIndividualSteps = config.getBoolean("zuchini.reportIndividualSteps").orElse(false);

        ZuchiniTestDescriptor rootTestDescriptor = (ZuchiniTestDescriptor) executionRequest.getRootTestDescriptor();
        EngineExecutionListener engineExecutionListener = executionRequest.getEngineExecutionListener();

        rootTestDescriptor.run(engineExecutionListener);
    }
}
