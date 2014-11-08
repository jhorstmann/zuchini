package org.zuchini.intellij;

import org.junit.runner.RunWith;
import org.zuchini.junit.Zuchini;
import org.zuchini.junit.ZuchiniOptions;

@RunWith(Zuchini.class)
@ZuchiniOptions(featurePackages = {"org/zuchini/intellij"}, stepDefinitionPackages = "org.zuchini.intellij",
        reportIndividualSteps = true, listeners = {DebugListener.class})
public class CukesJunitTest {
}
