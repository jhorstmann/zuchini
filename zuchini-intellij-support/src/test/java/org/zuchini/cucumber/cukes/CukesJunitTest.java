package org.zuchini.cucumber.cukes;

import org.junit.runner.RunWith;
import org.zuchini.junit.Zuchini;
import org.zuchini.junit.ZuchiniOptions;

@RunWith(Zuchini.class)
@ZuchiniOptions(featurePackages = {"org/zuchini/cucumber"}, stepDefinitionPackages = "org.zuchini.cucumber.cukes",
        reportIndividualSteps = true, listeners = {DebugListener.class})
public class CukesJunitTest {
}
