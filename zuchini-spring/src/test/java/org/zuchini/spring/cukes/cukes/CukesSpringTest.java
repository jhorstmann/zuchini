package org.zuchini.spring.cukes.cukes;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.zuchini.junit.ZuchiniOptions;
import org.zuchini.spring.SpringZuchini;
import org.zuchini.spring.ScenarioScopeConfiguration;

@RunWith(SpringZuchini.class)
@ZuchiniOptions(featurePackages = {"features/cukes"}, stepDefinitionPackages = "org.zuchini.spring.cukes",
        reportIndividualSteps = true, listeners = {SpringAutowiredRunListener.class})
@ContextConfiguration(classes = {ScenarioScopeConfiguration.class, CukesConfiguration.class})
public class CukesSpringTest {
}
