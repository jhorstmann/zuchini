package net.jhorstmann.gherkin;

import net.jhorstmann.gherkin.model.*;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class ParserTest {

    @Test
    public void shouldParseSimpleScenario() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Simple Feature\n\nScenario: Simple Scenario\nGiven a customer\n");

        assertEquals(1, feature.getLineNumber());
        assertEquals("Feature", feature.getKeyword());
        assertEquals("Simple Feature", feature.getDescription());

        StepContainer scenario = feature.getScenarios().get(0);
        assertEquals(3, scenario.getLineNumber());
        assertEquals("Scenario", scenario.getKeyword());
        assertEquals("Simple Scenario", scenario.getDescription());

        List<Step> steps = scenario.getSteps();
        {
            Step step = steps.get(0);
            assertEquals(4, step.getLineNumber());
            assertEquals("Given", step.getKeyword());
            assertEquals("a customer", step.getDescription());
        }
    }

    @Test
    public void shouldAllowKeywordsInContent() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Keywords\n\nScenario: Background Given When Then\nGiven Given Examples:\n");

        assertEquals(1, feature.getLineNumber());
        assertEquals("Feature", feature.getKeyword());
        assertEquals("Keywords", feature.getDescription());

        StepContainer scenario = feature.getScenarios().get(0);
        assertEquals("Scenario", scenario.getKeyword());
        assertEquals("Background Given When Then", scenario.getDescription());

        List<Step> steps = scenario.getSteps();
        {
            Step step = steps.get(0);
            assertEquals(4, step.getLineNumber());
            assertEquals("Given", step.getKeyword());
            assertEquals("Given Examples:", step.getDescription());
        }
    }

    @Test
    public void shouldParseBackground() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Simple Feature\n\nBackground: Simple Background\nGiven a customer\n");

        assertEquals(1, feature.getLineNumber());
        assertEquals("Feature", feature.getKeyword());
        assertEquals("Simple Feature", feature.getDescription());

        StepContainer scenario = feature.getBackground().get(0);
        assertEquals(3, scenario.getLineNumber());
        assertEquals("Background", scenario.getKeyword());
        assertEquals("Simple Background", scenario.getDescription());

        List<Step> steps = scenario.getSteps();
        {
            Step step = steps.get(0);
            assertEquals(4, step.getLineNumber());
            assertEquals("Given", step.getKeyword());
            assertEquals("a customer", step.getDescription());
        }
    }

    @Test
    public void shouldParseMultiLineFeatures() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Checkout\nAs a customer\nI want to place orders\n\n");

        assertEquals(1, feature.getLineNumber());
        assertEquals("Feature", feature.getKeyword());
        assertEquals("Checkout\nAs a customer\nI want to place orders", feature.getDescription());
    }

    @Test
    public void shouldParseFeatureComments() {
        Feature feature = FeatureParser.getFeature(
                "\n#Comment 1\n#Comment 2\n\n#Comment 3\n\nFeature: Commented Feature\n");

        assertEquals(7, feature.getLineNumber());
        assertEquals("Feature", feature.getKeyword());
        assertEquals("Commented Feature", feature.getDescription());

        assertEquals(asList("Comment 1", "Comment 2", "Comment 3"), feature.getComments());
    }

    @Test
    public void shouldParseStepComments() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Comments\n\nScenario: Commented steps\n# Comment 1\nGiven a commented step\n# Comment 2\nThen the comment is parsed\n");

        StepContainer scenario = feature.getScenarios().get(0);

        List<Step> steps = scenario.getSteps();
        {
            Step step = steps.get(0);
            assertEquals(5, step.getLineNumber());
            assertEquals("Given", step.getKeyword());
            assertEquals("a commented step", step.getDescription());
            assertEquals(asList("Comment 1"), step.getComments());
        }
        {
            Step step = steps.get(1);
            assertEquals(7, step.getLineNumber());
            assertEquals("Then", step.getKeyword());
            assertEquals("the comment is parsed", step.getDescription());
            assertEquals(asList("Comment 2"), step.getComments());
        }
    }

    @Test
    public void shouldParseStepTables() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Tables\n\nScenario: Step with table\nGiven a table:\n  | A | B |\n  | 1 | 2 |\n");

        StepContainer scenario = feature.getScenarios().get(0);

        List<Step> steps = scenario.getSteps();
        {
            Step step = steps.get(0);
            assertEquals("Given", step.getKeyword());
            assertEquals("a table:", step.getDescription());
            List<Row> rows = step.getRows();

            {
                Row row = rows.get(0);
                assertEquals(asList("A", "B"), row.getCells());
            }
            {
                Row row = rows.get(1);
                assertEquals(asList("1", "2"), row.getCells());
            }
        }
    }

    @Test
    public void shouldParseEscapedPipesInTable() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Tables\n\nScenario: Step with table\nGiven a table:\n  |  A  |\n  | X\\|X |\n");

        StepContainer scenario = feature.getScenarios().get(0);

        List<Step> steps = scenario.getSteps();
        {
            Step step = steps.get(0);
            assertEquals("Given", step.getKeyword());
            assertEquals("a table:", step.getDescription());
            List<Row> rows = step.getRows();

            {
                Row row = rows.get(0);
                assertEquals(asList("A"), row.getCells());
            }
            {
                Row row = rows.get(1);
                assertEquals(asList("X|X"), row.getCells());
            }
        }
    }

    @Test
    public void shouldParseOutlineExamples() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Outline\n\nScenario Outline: Scenario outline\nGiven a customer from <Country>\nExamples:\n| Country |\n| DE |\n");

        assertEquals("Feature", feature.getKeyword());
        assertEquals("Outline", feature.getDescription());

        StepContainer scenario = feature.getScenarios().get(0);
        assertEquals("Scenario Outline", scenario.getKeyword());
        assertEquals("Scenario outline", scenario.getDescription());

        List<Row> examples = ((Outline) scenario).getExamples();
        {
            Row row = examples.get(0);
            assertEquals(asList("Country"), row.getCells());
        }
        {
            Row row = examples.get(1);
            assertEquals(asList("DE"), row.getCells());
        }
    }

    @Test
    public void shouldParseTags() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Tags\n\n@TaggedScenario\nScenario: Tags on scenario and steps\n@TaggedStep\nGiven a tagged step\n");

        assertEquals("Feature", feature.getKeyword());
        assertEquals("Tags", feature.getDescription());

        StepContainer scenario = feature.getScenarios().get(0);
        assertEquals("Scenario", scenario.getKeyword());
        assertEquals(asList("TaggedScenario"), scenario.getTags());

        Step step = scenario.getSteps().get(0);

        assertEquals("Given", step.getKeyword());
        assertEquals("a tagged step", step.getDescription());
        assertEquals(asList("TaggedStep"), step.getTags());
    }

    @Test
    public void shouldParseMultipleTags() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Tags\n\n@Tag1 @Tag2\nScenario: Tags on scenario and steps\n@Tag3\n@Tag4 @Tag5\nGiven a tagged step\n");

        assertEquals("Feature", feature.getKeyword());
        assertEquals("Tags", feature.getDescription());

        StepContainer scenario = feature.getScenarios().get(0);
        assertEquals("Scenario", scenario.getKeyword());
        assertEquals(asList("Tag1", "Tag2"), scenario.getTags());

        Step step = scenario.getSteps().get(0);

        assertEquals("Given", step.getKeyword());
        assertEquals("a tagged step", step.getDescription());
        assertEquals(asList("Tag3", "Tag4", "Tag5"), step.getTags());
    }

    @Test
    public void shouldParseDocuments() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Documents\n\nScenario: Documents\nGiven the following string:\n\"\"\"ABC\nDEF\nGHI\"\"\"\n");

        assertEquals("Feature", feature.getKeyword());
        assertEquals("Documents", feature.getDescription());

        StepContainer scenario = feature.getScenarios().get(0);

        Step step = scenario.getSteps().get(0);

        assertEquals("Given", step.getKeyword());
        assertEquals("the following string:", step.getDescription());
        assertEquals(asList("ABC\nDEF\nGHI"), step.getDocs());
    }

}
