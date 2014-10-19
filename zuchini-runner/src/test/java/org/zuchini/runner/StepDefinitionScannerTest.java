package org.zuchini.runner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.zuchini.runner.steps.compat.CompatSteps;
import org.zuchini.runner.steps.normal.NormalSteps;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class StepDefinitionScannerTest {

    private static Map<String, StepDefinition> byMethodName(List<StepDefinition> stepDefinitions) {
        Map<String, StepDefinition> result = new HashMap<>();
        for (StepDefinition stepDefinition : stepDefinitions) {
            result.put(stepDefinition.getMethod().getName(), stepDefinition);
        }
        return result;
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> parameters() {
        return asList(new Object[]{NormalSteps.class}, new Object[]{CompatSteps.class});
    }

    private final Class<?> stepClass;

    public StepDefinitionScannerTest(Class<?> stepClass) {
        this.stepClass = stepClass;
    }

    @Test
    public void shouldSupportAnnotations() throws IOException {
        ClassLoader cl = StepDefinitionScannerTest.class.getClassLoader();
        String pkg = stepClass.getPackage().getName();
        List<StepDefinition> stepDefinitions = StepDefinitionScanner.scan(cl, pkg);

        assertEquals(3, stepDefinitions.size());

        Map<String, StepDefinition> stepsByMethodName = byMethodName(stepDefinitions);

        StepDefinition given = stepsByMethodName.get("given");
        assertNotNull(given);
        assertEquals(stepClass, given.getMethod().getDeclaringClass());

        StepDefinition then = stepsByMethodName.get("then");
        assertNotNull(then);
        assertEquals(stepClass, then.getMethod().getDeclaringClass());

        StepDefinition when = stepsByMethodName.get("when");
        assertNotNull(when);
        assertEquals(stepClass, when.getMethod().getDeclaringClass());
    }


}
