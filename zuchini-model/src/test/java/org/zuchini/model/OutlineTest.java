package org.zuchini.model;

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
        given.getDocs().add("Document <C>");
        outline.getSteps().add(given);

        Examples examples = new Examples(outline, 0, "Examples", "");
        examples.getTags().add("TaggedExample");

        Row header = new Row(feature, 3, "A", "B", "C");
        examples.getRows().add(header);

        Row row1 = new Row(feature, 4, "1", "2", "3");
        row1.getTags().add("TaggedExampleRow");
        examples.getRows().add(row1);

        Row example2 = new Row(feature, 5, "11", "12", "13");
        example2.getTags().add("TaggedExampleRow");
        examples.getRows().add(example2);

        outline.getExamples().add(examples);

        List<Scenario> scenarios = outline.buildScenarios();

        assertEquals(2, scenarios.size());
        {
            Scenario scenario = scenarios.get(0);
            assertEquals("Outline {A=1, B=2, C=3}", scenario.getDescription());
            assertEquals(4, scenario.getLineNumber());
            assertEquals(asList("TaggedOutline", "TaggedExample", "TaggedExampleRow"), scenario.getTags());

            List<Step> steps = scenario.getSteps();
            assertEquals(1, steps.size());

            Step step = steps.get(0);
            assertEquals("Given", step.getKeyword());
            assertEquals("values 1 and 2", step.getDescription());
            assertEquals(asList("TaggedStep"), step.getTags());
            assertEquals(asList("Document 3"), step.getDocs());
        }
        {
            Scenario scenario = scenarios.get(1);
            assertEquals("Outline {A=11, B=12, C=13}", scenario.getDescription());
            assertEquals(5, scenario.getLineNumber());
            assertEquals(asList("TaggedOutline", "TaggedExample", "TaggedExampleRow"), scenario.getTags());

            List<Step> steps = scenario.getSteps();
            assertEquals(1, steps.size());

            Step step = steps.get(0);
            assertEquals("Given", step.getKeyword());
            assertEquals("values 11 and 12", step.getDescription());
            assertEquals(asList("TaggedStep"), step.getTags());
            assertEquals(asList("Document 13"), step.getDocs());
        }

    }
}
