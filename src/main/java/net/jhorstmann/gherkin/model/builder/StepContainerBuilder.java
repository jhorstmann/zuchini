package net.jhorstmann.gherkin.model.builder;

public class StepContainerBuilder<T> {
    private FeatureBuilder featureBuilder;

    public StepContainerBuilder(FeatureBuilder featureBuilder) {
        this.featureBuilder = featureBuilder;
    }

}
