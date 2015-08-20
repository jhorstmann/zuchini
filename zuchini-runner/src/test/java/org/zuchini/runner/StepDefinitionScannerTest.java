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

    private static Map<String, StepDefinition> byMethodName(final List<StepDefinition> stepDefinitions) {
        Map<String, StepDefinition> result = new HashMap<>();
        for (StepDefinition stepDefinition : stepDefinitions) {
            result.put(stepDefinition.getMethod().getName(), stepDefinition);
        }
        return result;
    }

    private static Map<String, HookDefinition> byTag(final List<HookDefinition> hookDefinitions) {
        Map<String, HookDefinition> result = new HashMap<>();
        for (HookDefinition hookDefinition : hookDefinitions) {
            for (String tag : hookDefinition.getTags()) {
                result.put(tag, hookDefinition);
            }
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
        final ClassLoader cl = StepDefinitionScannerTest.class.getClassLoader();
        final String pkg = stepClass.getPackage().getName();
        final StepDefinitionScanner scanner = new StepDefinitionScanner(cl, asList(pkg));

        scanner.scan();

        {
            final List<StepDefinition> stepDefinitions = scanner.getStepDefinitions();

            assertEquals(3, stepDefinitions.size());

            final Map<String, StepDefinition> stepsByMethodName = byMethodName(stepDefinitions);

            final StepDefinition given = stepsByMethodName.get("given");
            assertNotNull(given);
            assertEquals(stepClass, given.getMethod().getDeclaringClass());

            final StepDefinition then = stepsByMethodName.get("then");
            assertNotNull(then);
            assertEquals(stepClass, then.getMethod().getDeclaringClass());

            final StepDefinition when = stepsByMethodName.get("when");
            assertNotNull(when);
            assertEquals(stepClass, when.getMethod().getDeclaringClass());
        }

        {
            final List<HookDefinition> hookDefinitions = scanner.getHookDefinitions();

            assertEquals(2, hookDefinitions.size());

            final Map<String, HookDefinition> hookByTag = byTag(hookDefinitions);

            final HookDefinition before = hookByTag.get("beforeTag");
            assertNotNull(before);
            assertEquals(stepClass, before.getMethod().getDeclaringClass());

            final HookDefinition after = hookByTag.get("afterTag");
            assertNotNull(after);
            assertEquals(stepClass, after.getMethod().getDeclaringClass());
        }
    }


}
