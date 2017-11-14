package org.zuchini.junit5.helloworld;


import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.zuchini.junit5.ZuchiniTestFactory;

import java.util.stream.Stream;

public class HelloWorldTest {
    @TestFactory
    public Stream<DynamicContainer> features() {
        return new ZuchiniTestFactory()
                .withFeaturePackages("org.zuchini.junit5.helloworld")
                .withStepDefinitionPackages("org.zuchini.junit5.helloworld")
                .withReportIndividualSteps(true)
                .features();
    }

    @Test
    public void anotherTest() {

    }
}
