package org.zuchini.junit.cukes;

import org.junit.runner.RunWith;
import org.zuchini.junit.Zuchini;
import org.zuchini.junit.ZuchiniOptions;

@RunWith(Zuchini.class)
@ZuchiniOptions(featurePackages = {"features/cukes"}, stepDefinitionPackages = "org.zuchini.junit.cukes")
public class CukesJunitTest {
}
