package org.zuchini.compat.ast;

import gherkin.ast.ArgumentNode;
import gherkin.ast.BackgroundNode;
import gherkin.ast.CommentNode;
import gherkin.ast.DataTableNode;
import gherkin.ast.DocStringNode;
import gherkin.ast.ExamplesNode;
import gherkin.ast.FeatureNode;
import gherkin.ast.Location;
import gherkin.ast.ScenarioDefinitionNode;
import gherkin.ast.ScenarioNode;
import gherkin.ast.ScenarioOutlineNode;
import gherkin.ast.StepNode;
import gherkin.ast.TableCellNode;
import gherkin.ast.TableRowNode;
import gherkin.ast.TagNode;
import org.zuchini.model.Background;
import org.zuchini.model.Examples;
import org.zuchini.model.Feature;
import org.zuchini.model.LocationAware;
import org.zuchini.model.Outline;
import org.zuchini.model.Row;
import org.zuchini.model.Scenario;
import org.zuchini.model.Step;
import org.zuchini.model.StepContainer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class AstBuilder {

    private static Location location(LocationAware locationAware) {
        return new Location(locationAware.getLineNumber(), 0);
    }

    private static Location location(int lineNumber) {
        return new Location(lineNumber, 0);
    }

    private static TableRowNode toTableRowNode(Row row) {
        return new TableRowNode(
                location(row),
                row.getCells().stream().map(cell -> new TableCellNode(location(row), cell)).collect(toList()));
    }

    private static StepNode toStepNode(Step step) {
        Optional<ArgumentNode> docNode = step.getDocs().stream().findFirst().map(doc -> new DocStringNode(location(step.getLineNumber()+1), "", doc));
        Optional<ArgumentNode> tableNode = step.getRows().isEmpty()
                ? Optional.empty()
                : Optional.of(new DataTableNode(
                step.getRows().stream().map(AstBuilder::toTableRowNode).collect(toList())));

        return new StepNode(
                location(step),
                step.getKeyword(),
                step.getName(),
                docNode.isPresent() ? docNode.get() : (tableNode.isPresent() ? tableNode.get() : null));
    }

    private static BackgroundNode toBackgroundNode(Background background) {
        return new BackgroundNode(
                location(background),
                background.getKeyword(),
                background.getName(),
                null,
                background.getSteps().stream().map(AstBuilder::toStepNode).collect(toList()));
    }

    private static ExamplesNode toExamplesNode(Examples examples) {
        return new ExamplesNode(
                location(examples),
                examples.getTags().stream().map(tag -> new TagNode(location(examples), "@"+tag)).collect(toList()),
                examples.getKeyword(),
                examples.getName(),
                null,
                examples.getRows().stream().findFirst().map(AstBuilder::toTableRowNode).get(),
                examples.getRows().stream().skip(1).map(AstBuilder::toTableRowNode).collect(toList()));
    }

    private static ScenarioDefinitionNode toScenarioDefinitionNode(StepContainer stepContainer) {
        if (stepContainer instanceof Scenario) {
            return new ScenarioNode(
                    stepContainer.getTags().stream().map(tag -> new TagNode(location(stepContainer), "@"+tag)).collect(toList()),
                    location(stepContainer),
                    stepContainer.getKeyword(),
                    stepContainer.getName(),
                    null,
                    stepContainer.getSteps().stream().map(AstBuilder::toStepNode).collect(toList()));
        } else if (stepContainer instanceof Outline) {
            return new ScenarioOutlineNode(
                    stepContainer.getTags().stream().map(tag -> new TagNode(location(stepContainer), "@"+tag)).collect(toList()),
                    location(stepContainer),
                    stepContainer.getKeyword(),
                    stepContainer.getName(),
                    null,
                    stepContainer.getSteps().stream().map(AstBuilder::toStepNode).collect(toList()),
                    ((Outline)stepContainer).getExamples().stream().map(AstBuilder::toExamplesNode).collect(toList()));

        } else {
            throw new IllegalStateException();
        }
    }

    private static Stream<String> toComments(StepContainer stepContainer) {
        return Stream.concat(
                stepContainer.getComments().stream(),
                stepContainer.getSteps().stream().flatMap(
                        step -> Stream.concat(step.getComments().stream(), step.getRows().stream().flatMap(row -> row.getComments().stream()))));
    }

    public static FeatureNode toFeatureNode(Feature feature) {

        final List<String> comments = Stream.concat(
                feature.getComments().stream(),
                Stream.concat(
                        feature.getBackground().stream(),
                        feature.getScenarios().stream()).flatMap(AstBuilder::toComments)).collect(toList());

        return new FeatureNode(
                feature.getTags().stream().map(tag -> new TagNode(location(feature), "@"+tag)).collect(toList()),
                location(feature),
                "en",
                feature.getKeyword(),
                feature.getName(),
                null,
                feature.getBackground().stream().findFirst().map(AstBuilder::toBackgroundNode).orElse(null),
                feature.getScenarios().stream().map(AstBuilder::toScenarioDefinitionNode).collect(toList()),
                comments.stream().map(comment -> new CommentNode(location(feature), comment)).collect(toList()));
    }
}
