package org.zuchini.examples.mockmvc;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.zuchini.junit.ZuchiniOptions;
import org.zuchini.spring.SpringZuchini;

@RunWith(SpringZuchini.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ZuchiniOptions(featurePackages = {"features/mockmvc"},
        stepDefinitionPackages = "org.zuchini.examples.mockmvc",
        reportIndividualSteps = true)
public class HelloFeatureTest {
}
