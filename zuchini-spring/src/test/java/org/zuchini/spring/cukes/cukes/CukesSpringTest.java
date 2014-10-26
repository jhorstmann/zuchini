package org.zuchini.spring.cukes.cukes;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.zuchini.junit.ZuchiniOptions;
import org.zuchini.spring.SpringZuchini;
import org.zuchini.spring.SpringZuchiniConfiguration;

@RunWith(SpringZuchini.class)
@ZuchiniOptions(featurePackages = {"features/cukes"}, stepDefinitionPackages = "org.zuchini.spring.cukes",
        reportIndividualSteps = true)
@ContextConfiguration(classes = {SpringZuchiniConfiguration.class, CukesConfiguration.class})
public class CukesSpringTest {
}
