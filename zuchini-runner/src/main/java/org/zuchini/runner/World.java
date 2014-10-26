package org.zuchini.runner;

import java.util.List;

public class World {
    private final ConverterConfiguration converterConfiguration;
    private final List<FeatureStatement> featureStatements;

    World(ConverterConfiguration converterConfiguration, List<FeatureStatement> featureStatements) {
        this.converterConfiguration = converterConfiguration;
        this.featureStatements = featureStatements;
    }

    public ConverterConfiguration getConverterConfiguration() {
        return converterConfiguration;
    }

    public List<FeatureStatement> getFeatureStatements() {
        return featureStatements;
    }


}
