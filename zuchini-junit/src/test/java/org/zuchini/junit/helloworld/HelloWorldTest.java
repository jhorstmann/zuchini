package org.zuchini.junit.helloworld;

import org.junit.runner.RunWith;
import org.zuchini.junit.Zuchini;
import org.zuchini.junit.ZuchiniOptions;

@RunWith(Zuchini.class)
@ZuchiniOptions(featurePackages = {"features/helloworld"}, stepDefinitionPackages = "org.zuchini.junit.helloworld", reportIndividualSteps = true)
public class HelloWorldTest {
}
