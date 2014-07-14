package org.zuchini.gherkin;

import org.zuchini.gherkin.model.*;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class OutlineTest {
    @Test
    public void shouldExpandExamples() {
        Feature feature = new Feature("", 1, "Feature", "Outlines should get expanded");
        Outline outline = new Outline(feature, 1, "Scenario Outline", "Outline");
        outline.getTags().add("TaggedOutline");
        Step given = new Step(outline, 2, "Given", "values <A> and <B>");
        given.getTags().add("TaggedStep");
        outline.getSteps().add(given);

        Row header = new Row(feature, 3, "A", "B");
        outline.getExamples().add(header);

        Row example1 = new Row(feature, 4, "1", "2");
        example1.getTags().add("TaggedExample");
        outline.getExamples().add(example1);

        Row example2 = new Row(feature, 5, "3", "4");
        example2.getTags().add("TaggedExample");
        outline.getExamples().add(example2);

        List<Scenario> scenarios = outline.buildScenarios();

        assertEquals(2, scenarios.size());
        {
            Scenario scenario = scenarios.get(0);
            assertEquals("Outline {A=1, B=2}", scenario.getDescription());
            assertEquals(4, scenario.getLineNumber());
            assertEquals(asList("TaggedOutline", "TaggedExample"), scenario.getTags());

            List<Step> steps = scenario.getSteps();
            assertEquals(1, steps.size());

            Step step = steps.get(0);
            assertEquals("Given", step.getKeyword());
            assertEquals("values 1 and 2", step.getDescription());
            assertEquals(asList("TaggedStep", "TaggedExample"), step.getTags());
        }
        {
            Scenario scenario = scenarios.get(1);
            assertEquals("Outline {A=3, B=4}", scenario.getDescription());
            assertEquals(5, scenario.getLineNumber());
            assertEquals(asList("TaggedOutline", "TaggedExample"), scenario.getTags());

            List<Step> steps = scenario.getSteps();
            assertEquals(1, steps.size());

            Step step = steps.get(0);
            assertEquals("Given", step.getKeyword());
            assertEquals("values 3 and 4", step.getDescription());
            assertEquals(asList("TaggedStep", "TaggedExample"), step.getTags());
        }

    }
}
