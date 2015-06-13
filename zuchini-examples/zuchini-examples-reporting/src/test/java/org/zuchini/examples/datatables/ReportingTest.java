package org.zuchini.examples.datatables;

import org.junit.runner.RunWith;
import org.zuchini.junit.Zuchini;
import org.zuchini.junit.ZuchiniOptions;
import org.zuchini.reporter.JsonReporter;

@RunWith(Zuchini.class)
@ZuchiniOptions(stepDefinitionPackages = {"org.zuchini.examples"}, featurePackages = {"features"}, reportIndividualSteps = false, listeners = JsonReporter.class)
public class ReportingTest {
}
