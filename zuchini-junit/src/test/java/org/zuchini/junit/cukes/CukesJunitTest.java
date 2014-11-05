package org.zuchini.junit.cukes;

import org.junit.runner.RunWith;
import org.zuchini.junit.Zuchini;
import org.zuchini.junit.ZuchiniOptions;
import org.zuchini.junit.rerun.RerunListener;

@RunWith(Zuchini.class)
@ZuchiniOptions(featurePackages = {"features/cukes"}, stepDefinitionPackages = "org.zuchini.junit.cukes",
        reportIndividualSteps = true, listeners = {RerunListener.class})
public class CukesJunitTest {
}
