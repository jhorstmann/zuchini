package org.zuchini.parser;

import org.zuchini.model.*;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    public void shouldParseEmptyScenario() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Should support emtpy scenario\n\nScenario: Empty scenario\n");

        assertEquals(1, feature.getLineNumber());
        assertEquals("Feature", feature.getKeyword());
        assertEquals("Should support emtpy scenario", feature.getDescription());

        StepContainer scenario = feature.getScenarios().get(0);
        assertEquals(3, scenario.getLineNumber());
        assertEquals("Scenario", scenario.getKeyword());
        assertEquals("Empty scenario", scenario.getDescription());

        assertNotNull(scenario.getSteps());
        assertEquals(0, scenario.getSteps().size());
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
    public void shouldParseMixedTagsAndComments() {
        Feature feature = FeatureParser.getFeature(
                "@Tag1\n#Comment 1\n@Tag2   @Tag3\n#Comment 2\n\n#Comment 3\n@Tag4\n\n@Tag5\nFeature: Tagged and Commented Feature\n");

        assertEquals(10, feature.getLineNumber());
        assertEquals("Feature", feature.getKeyword());
        assertEquals("Tagged and Commented Feature", feature.getDescription());

        assertEquals(asList("Comment 1", "Comment 2", "Comment 3"), feature.getComments());
        assertEquals(asList("Tag1", "Tag2", "Tag3", "Tag4", "Tag5"), feature.getTags());
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
            assertEquals(asList(" Comment 1"), step.getComments());
        }
        {
            Step step = steps.get(1);
            assertEquals(7, step.getLineNumber());
            assertEquals("Then", step.getKeyword());
            assertEquals("the comment is parsed", step.getDescription());
            assertEquals(asList(" Comment 2"), step.getComments());
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
    public void shouldSupportedTrailingSpacesAfterRows() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Tables\n\nScenario: Step with table\nGiven a table:\n  | A | B |\t\n  | 1 | 2 |  \n");

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

        List<Row> examples = ((Outline) scenario).getExampleRows();
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
    public void shouldParseMultipleOutlineExamples() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Outline\n\nScenario Outline: Scenario outline\nGiven a customer from <Country>\nExamples: Europa\n| Country |\n| DE |\nExamples: America\n| Country |\n| US |\n");

        assertEquals("Feature", feature.getKeyword());
        assertEquals("Outline", feature.getDescription());

        Outline outline = (Outline) feature.getScenarios().get(0);
        assertEquals("Scenario Outline", outline.getKeyword());
        assertEquals("Scenario outline", outline.getDescription());


        List<Examples> examples = outline.getExamples();
        assertEquals(2, examples.size());
        {
            Examples example = examples.get(0);
            assertEquals("Europa", example.getDescription());
            List<Row> rows = example.getRows();
            assertEquals(2, rows.size());
            assertEquals(asList("Country"), rows.get(0).getCells());
            assertEquals(asList("DE"), rows.get(1).getCells());
        }
        {
            Examples example = examples.get(1);
            assertEquals("America", example.getDescription());
            List<Row> rows = example.getRows();
            assertEquals(2, rows.size());
            assertEquals(asList("Country"), rows.get(0).getCells());
            assertEquals(asList("US"), rows.get(1).getCells());
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
    public void shouldParseMultipleTagsWithWhitespace() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Tags\n\n@Tag1    @Tag2\nScenario: Tags on scenario and steps\n\t@Tag3\n  @Tag4 @Tag5\n\n  Given a tagged step\n");

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
    public void shouldParseMultipleTagsWithTrailingWhitespace() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Tags\n\n@Tag1 @Tag2 \nScenario: Tags on scenario and steps\n@Tag3 \n  @Tag4  @Tag5   \n\n  Given a tagged step\n");

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
    public void shouldParseTagsOnBackground() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Tagged Background\n\n@Test\nBackground: Tagged Background\nGiven a customer\n");

        StepContainer background = feature.getBackground().get(0);
        assertEquals(4, background.getLineNumber());
        assertEquals("Background", background.getKeyword());
        assertEquals("Tagged Background", background.getDescription());
        assertEquals(asList("Test"), background.getTags());
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

    @Test
    public void shouldStripIndentFromDocuments() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Documents\n\nScenario: Documents\nGiven the following string:\n  \"\"\"  ABC\n    DEF\n    GHI\"\"\"\n");

        assertEquals("Feature", feature.getKeyword());
        assertEquals("Documents", feature.getDescription());

        StepContainer scenario = feature.getScenarios().get(0);

        Step step = scenario.getSteps().get(0);

        assertEquals("Given", step.getKeyword());
        assertEquals("the following string:", step.getDescription());
        assertEquals(asList("ABC\n  DEF\n  GHI"), step.getDocs());
    }

    @Test
    public void shouldStripIndentFromDocumentsAndRetainLineBreaks() {
        Feature feature = FeatureParser.getFeature(
                "Feature: Documents\r\n\r\nScenario: Documents\r\nGiven the following string:\r\n\t\"\"\"\tABC\r\n\t\tDEF\r\n\t\tGHI\r\n\"\"\"\r\n");

        assertEquals("Feature", feature.getKeyword());
        assertEquals("Documents", feature.getDescription());

        StepContainer scenario = feature.getScenarios().get(0);

        Step step = scenario.getSteps().get(0);

        assertEquals("Given", step.getKeyword());
        assertEquals("the following string:", step.getDescription());
        assertEquals(asList("ABC\r\n\tDEF\r\n\tGHI\r\n"), step.getDocs());
    }

    @Test
    public void shouldIgnoreTrailingComments() {
        Feature feature = FeatureParser.getFeature(
                "#header comment\nFeature: Trailing comments\n\nScenario: Scenario\nGiven a step\n\n#trailing comment\n");

        assertEquals("Feature", feature.getKeyword());
        assertEquals(asList("header comment"), feature.getComments());

        StepContainer scenario = feature.getScenarios().get(0);
        assertEquals(asList(), scenario.getComments());

        Step step = scenario.getSteps().get(0);
        assertEquals(asList(), step.getComments());
    }

}
