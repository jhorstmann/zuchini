package org.zuchini.parser;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.zuchini.gherkin.antlr.GherkinBaseVisitor;
import org.zuchini.gherkin.antlr.GherkinParser;
import org.zuchini.model.Background;
import org.zuchini.model.Examples;
import org.zuchini.model.Feature;
import org.zuchini.model.Outline;
import org.zuchini.model.Row;
import org.zuchini.model.Scenario;
import org.zuchini.model.Step;
import org.zuchini.model.StepContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.zuchini.parser.ParserHelper.visitNodes;
import static org.zuchini.parser.ParserHelper.visitNodesAndAggregate;
import static org.zuchini.parser.ParserHelper.visitOptionalNode;

public class Visitors  {

    private Visitors() {

    }

    static class AggregatingVisitor<T> extends GherkinBaseVisitor<List<T>> {

        private static final Set<Class<?>> IMMUTABLE_LIST_CLASSES = new HashSet<Class<?>>(asList(Collections.emptyList().getClass(),
                Collections.singletonList(null).getClass(), Arrays.asList().getClass()));

        private static <T> boolean isMutable(List<T> list) {
            Class<? extends List> listClass = list.getClass();
            return !IMMUTABLE_LIST_CLASSES.contains(listClass);
        }

        @Override
        protected List<T> defaultResult() {
            return Collections.emptyList();
        }

        @Override
        protected List<T> aggregateResult(List<T> aggregate, List<T> nextResult) {
            if (aggregate.isEmpty()) {
                return nextResult;
            } else if (nextResult.isEmpty()) {
                return aggregate;
            } else if (isMutable(aggregate)) {
                aggregate.addAll(nextResult);
                return aggregate;
            } else {
                List<T> result = new ArrayList<>(aggregate.size()+nextResult.size()+2);
                result.addAll(aggregate);
                result.addAll(nextResult);
                return result;
            }
        }
    }

    static class TagsVisitor extends AggregatingVisitor<String> {

        @Override
        public List<String> visitTagName(@NotNull GherkinParser.TagNameContext ctx) {
            return Collections.singletonList(ctx.getText());
        }

        @Override
        public List<String> visitTag(@NotNull GherkinParser.TagContext ctx) {
            return visitTagName(ctx.tagName());
        }

    }

    static class CommentsVisitor extends AggregatingVisitor<String> {
        @Override
        public List<String> visitComment(@NotNull GherkinParser.CommentContext ctx) {
            return Collections.singletonList(ctx.lineContent().getText());
        }

    }

    static class DocumentsVisitor extends  AggregatingVisitor<String> {
        private static final Pattern NEWLINE = Pattern.compile("(?:\\n|\\r\\n)");

        @Override
        public List<String> visitDocument(@NotNull GherkinParser.DocumentContext ctx) {
            final String  indent = ctx.documentIndent().getText();
            final String text = ctx.documentContent().getText();
            if (indent != null && indent.length() > 0) {
                final StringBuilder sb = new StringBuilder(text.length());
                final StringTokenizer tok = new StringTokenizer(text, "\r\n", true);
                while (tok.hasMoreTokens()) {
                    final String line = tok.nextToken();
                    sb.append(line.startsWith(indent) ? line.substring(indent.length()) : line);
                }

                return Collections.singletonList(sb.toString());
            } else {
                return Collections.singletonList(text);
            }
        }
    }


    static class CellVisitor extends GherkinBaseVisitor<String> {
        @Override
        public final String visitCell(@NotNull GherkinParser.CellContext ctx) {
            return ParserHelper.trimCell(ctx);
        }

    }

    static class RowVisitor extends GherkinBaseVisitor<Row> {

        private final Feature feature;

        RowVisitor(Feature feature) {
            this.feature = feature;
        }

        @Override
        public Row visitRow(@NotNull GherkinParser.RowContext ctx) {
            List<String> cells = visitNodes(ctx.cell(), new CellVisitor());

            Row row = new Row(feature, ctx.getStart().getLine(), cells);
            row.getTags().addAll(visitNodesAndAggregate(ctx.annotation(), new TagsVisitor()));
            row.getComments().addAll(visitNodesAndAggregate(ctx.annotation(), new CommentsVisitor()));
            return row;
        }
    }

    static class ExamplesVisitor extends GherkinBaseVisitor<Examples> {
        protected final Outline outline;

        ExamplesVisitor(Outline outline) {
            this.outline = outline;
        }

        @Override
        public Examples visitExamples(@NotNull GherkinParser.ExamplesContext ctx) {
            Token keyword = ctx.EXAMPLES_KW().getSymbol();
            Examples examples = new Examples(outline, keyword.getLine(), keyword.getText(),
                    ctx.lineContent() == null ? "" : ctx.lineContent().getText());
            examples.getTags().addAll(visitNodesAndAggregate(ctx.annotation(), new TagsVisitor()));
            examples.getComments().addAll(visitNodesAndAggregate(ctx.annotation(), new CommentsVisitor()));
            examples.getRows().addAll(visitNodes(ctx.row(), new RowVisitor(outline.getFeature())));
            return examples;
        }
    }

    static class StepVisitor extends GherkinBaseVisitor<Step> {
        protected final StepContainer stepContainer;

        StepVisitor(StepContainer stepContainer) {
            this.stepContainer = stepContainer;
        }

        @Override
        public Step visitStep(@NotNull GherkinParser.StepContext ctx) {
            Token keyword = ctx.STEP_KW().getSymbol();
            Step step = new Step(stepContainer, keyword.getLine(), keyword.getText(), ctx.lineContent().getText());
            step.getTags().addAll(visitNodesAndAggregate(ctx.annotation(), new TagsVisitor()));
            step.getComments().addAll(visitNodesAndAggregate(ctx.annotation(), new CommentsVisitor()));
            step.getDocs().addAll(visitNodesAndAggregate(ctx.document(), new DocumentsVisitor()));
            step.getRows().addAll(visitNodes(ctx.row(), new RowVisitor(stepContainer.getFeature())));

            return step;
        }

    }

    static class BackgroundVisitor extends GherkinBaseVisitor<Background> {

        private final Feature feature;
        BackgroundVisitor(Feature feature) {
            this.feature = feature;
        }

        @Override
        public Background visitBackground(@NotNull GherkinParser.BackgroundContext ctx) {
            Token keyword = ctx.BACKGROUND_KW().getSymbol();
            final String description = ctx.lineContent() == null ? "Background" : ctx.lineContent().getText();
            Background background = new Background(feature, keyword.getLine(), keyword.getText(), description);
            background.getTags().addAll(visitNodesAndAggregate(ctx.annotation(), new TagsVisitor()));
            background.getComments().addAll(visitNodesAndAggregate(ctx.annotation(), new CommentsVisitor()));
            background.getSteps().addAll(visitNodes(ctx.step(), new StepVisitor(background)));

            return background;
        }
    }

    static class ScenarioVisitor extends GherkinBaseVisitor<StepContainer> {
        private final Feature feature;
        ScenarioVisitor(Feature feature) {
            this.feature = feature;
        }

        @Override
        public StepContainer visitScenario(@NotNull GherkinParser.ScenarioContext ctx) {
            Token keyword = ctx.SCENARIO_KW().getSymbol();
            Scenario scenario = new Scenario(feature, keyword.getLine(), keyword.getText(), ctx.lineContent().getText());
            scenario.getTags().addAll(visitNodesAndAggregate(ctx.annotation(), new TagsVisitor()));
            scenario.getComments().addAll(visitNodesAndAggregate(ctx.annotation(), new CommentsVisitor()));
            scenario.getSteps().addAll(visitNodes(ctx.step(), new StepVisitor(scenario)));
            return scenario;
        }

        @Override
        public StepContainer visitOutline(@NotNull GherkinParser.OutlineContext ctx) {
            Token keyword = ctx.OUTLINE_KW().getSymbol();
            Outline outline = new Outline(feature, keyword.getLine(), keyword.getText(), ctx.lineContent().getText());
            outline.getTags().addAll(visitNodesAndAggregate(ctx.annotation(), new TagsVisitor()));
            outline.getComments().addAll(visitNodesAndAggregate(ctx.annotation(), new CommentsVisitor()));
            outline.getSteps().addAll(visitNodes(ctx.step(), new StepVisitor(outline)));
            outline.getExamples().addAll(visitNodes(ctx.examples(), new ExamplesVisitor(outline)));
            return outline;
        }

    }

    static class FeatureVisitor extends GherkinBaseVisitor<Feature> {
        private final String uri;

        FeatureVisitor(String uri) {
            this.uri = uri;
        }

        @Override
        public Feature visitFeature(@NotNull GherkinParser.FeatureContext ctx) {
            Token keyword = ctx.FEATURE_KW().getSymbol();
            Feature feature = new Feature(uri, keyword.getLine(), keyword.getText(), ctx.lineContent().getText());
            feature.getTags().addAll(visitNodesAndAggregate(ctx.annotation(), new TagsVisitor()));
            feature.getComments().addAll(visitNodesAndAggregate(ctx.annotation(), new CommentsVisitor()));
            feature.getBackground().addAll(visitOptionalNode(ctx.background(), new BackgroundVisitor(feature)));
            feature.getScenarios().addAll(visitNodes(ctx.abstractScenario(), new ScenarioVisitor(feature)));

            return feature;
        }
    }
}
