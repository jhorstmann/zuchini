package net.jhorstmann.gherkin;

import net.jhorstmann.gherkin.antlr.GherkinListener;
import net.jhorstmann.gherkin.antlr.GherkinParser;
import net.jhorstmann.gherkin.model.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class FeatureWalker implements GherkinListener {
    private final String uri;
    private Feature feature;
    private StepContainer stepContainer;
    private Commented commentContainer;
    private Tagged tagContainer;
    private Step step;
    private List<Row> rows;
    private Row row;

    public FeatureWalker(String uri) {
        this.uri = uri;
    }

    private static String trimKeyword(Token token) {
        return token.getText().replaceAll("^[\t ]+|[\t :]+$", "");
    }

    private static String trimComment(GherkinParser.CommentContext comment) {
        return comment.lineContent().getText().replaceFirst("^[\t #]+", "");
    }

    private static String trimCell(GherkinParser.CellContext cell) {
        return cell.getText().replaceAll("^[\t ]+|[\t |]+$", "").replace("\\|", "|");
    }

    private static String trimTag(GherkinParser.TagContext tag) {
        return tag.getText().replaceFirst("^@", "");
    }

    private static String trimDocument(GherkinParser.DocumentContext ctx) {
        return ctx.documentContent().getText();
    }

    private static String joinLineContent(Iterable<GherkinParser.LineContentContext> lines) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<GherkinParser.LineContentContext> it = lines.iterator(); it.hasNext(); ) {
            GherkinParser.LineContentContext line = it.next();
            sb.append(line.getText());
            if (it.hasNext()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public Feature getFeature() {
        return feature;
    }

    @Override
    public void enterFeature(@NotNull GherkinParser.FeatureContext ctx) {
        Token keyword = ctx.FEATURE_KW().getSymbol();

        String description = joinLineContent(ctx.lineContent());
        commentContainer = feature = new Feature(uri, keyword.getLine(), trimKeyword(keyword), description);
    }

    @Override
    public void exitFeature(@NotNull GherkinParser.FeatureContext ctx) {
    }

    @Override
    public void enterBackground(@NotNull GherkinParser.BackgroundContext ctx) {
        Token keyword = ctx.BACKGROUND_KW().getSymbol();
        commentContainer = stepContainer = new Background(feature, keyword.getLine(), trimKeyword(keyword),
                ctx.lineContent().getText());
    }

    @Override
    public void exitBackground(@NotNull GherkinParser.BackgroundContext ctx) {
        feature.getBackground().add((Background) stepContainer);
        stepContainer = null;
        commentContainer = null;
        tagContainer = null;
    }

    @Override
    public void enterStep(@NotNull GherkinParser.StepContext ctx) {
        Token keyword = ctx.STEP_KW().getSymbol();
        step = new Step(stepContainer, keyword.getLine(), trimKeyword(keyword), ctx.lineContent().getText());
        commentContainer = step;
        tagContainer = step;

    }

    @Override
    public void exitStep(@NotNull GherkinParser.StepContext ctx) {
        stepContainer.getSteps().add(step);
        commentContainer = step = null;
    }

    @Override
    public void enterDocument(@NotNull GherkinParser.DocumentContext ctx) {
        String doc = trimDocument(ctx);
        step.getDocs().add(doc);
    }

    @Override
    public void exitDocument(@NotNull GherkinParser.DocumentContext ctx) {
    }

    @Override
    public void enterComments(@NotNull GherkinParser.CommentsContext ctx) {
    }

    @Override
    public void exitComments(@NotNull GherkinParser.CommentsContext ctx) {
    }

    @Override
    public void enterComment(@NotNull GherkinParser.CommentContext ctx) {
        commentContainer.getComments().add(trimComment(ctx));
    }

    @Override
    public void exitComment(@NotNull GherkinParser.CommentContext ctx) {
    }

    @Override
    public void enterTags(@NotNull GherkinParser.TagsContext ctx) {
    }

    @Override
    public void exitTags(@NotNull GherkinParser.TagsContext ctx) {
    }

    @Override
    public void enterTag(@NotNull GherkinParser.TagContext ctx) {
        tagContainer.getTags().add(trimTag(ctx));
    }

    @Override
    public void exitTag(@NotNull GherkinParser.TagContext ctx) {
    }

    @Override
    public void enterAbstractScenario(@NotNull GherkinParser.AbstractScenarioContext ctx) {
    }

    @Override
    public void exitAbstractScenario(@NotNull GherkinParser.AbstractScenarioContext ctx) {
    }

    @Override
    public void enterScenario(@NotNull GherkinParser.ScenarioContext ctx) {
        Token keyword = ctx.SCENARIO_KW().getSymbol();
        stepContainer = new Scenario(feature, keyword.getLine(), trimKeyword(keyword), ctx.lineContent().getText());
        commentContainer = stepContainer;
        tagContainer = stepContainer;
    }

    @Override
    public void exitScenario(@NotNull GherkinParser.ScenarioContext ctx) {
        feature.getScenarios().add(stepContainer);
        stepContainer = null;
        commentContainer = null;
        tagContainer = null;
    }

    @Override
    public void enterOutline(@NotNull GherkinParser.OutlineContext ctx) {
        Token keyword = ctx.OUTLINE_KW().getSymbol();
        stepContainer = new Outline(feature, keyword.getLine(), trimKeyword(keyword), ctx.lineContent().getText());
        commentContainer = stepContainer;
        tagContainer = stepContainer;
    }

    @Override
    public void exitOutline(@NotNull GherkinParser.OutlineContext ctx) {
        feature.getScenarios().add(stepContainer);
        stepContainer = null;
        commentContainer = null;
        tagContainer = null;
    }

    @Override
    public void enterTable(@NotNull GherkinParser.TableContext ctx) {
        rows = new ArrayList<>();
    }

    @Override
    public void exitTable(@NotNull GherkinParser.TableContext ctx) {
        step.getRows().addAll(rows);
        rows = null;
    }

    @Override
    public void enterRow(@NotNull GherkinParser.RowContext ctx) {
        row = new Row(feature, ctx.getStart().getLine());
        commentContainer = row;
        tagContainer = row;
    }

    @Override
    public void exitRow(@NotNull GherkinParser.RowContext ctx) {
        rows.add(row);
        row = null;
        commentContainer = null;
        tagContainer = null;
    }

    @Override
    public void enterCell(@NotNull GherkinParser.CellContext ctx) {
    }

    @Override
    public void exitCell(@NotNull GherkinParser.CellContext ctx) {
        row.getCells().add(trimCell(ctx));
    }

    @Override
    public void enterLineContent(@NotNull GherkinParser.LineContentContext ctx) {
    }

    @Override
    public void exitLineContent(@NotNull GherkinParser.LineContentContext ctx) {
    }

    @Override
    public void enterExamples(@NotNull GherkinParser.ExamplesContext ctx) {
        rows = new ArrayList<>();
    }

    @Override
    public void exitExamples(@NotNull GherkinParser.ExamplesContext ctx) {
        ((Outline) stepContainer).getExamples().addAll(rows);
        rows = null;
    }

    @Override
    public void enterDocumentContent(@NotNull GherkinParser.DocumentContentContext ctx) {
    }

    @Override
    public void exitDocumentContent(@NotNull GherkinParser.DocumentContentContext ctx) {
    }

    @Override
    public void enterStart(@NotNull GherkinParser.StartContext ctx) {
    }

    @Override
    public void exitStart(@NotNull GherkinParser.StartContext ctx) {
    }

    @Override
    public void enterTagName(@NotNull GherkinParser.TagNameContext ctx) {
    }

    @Override
    public void exitTagName(@NotNull GherkinParser.TagNameContext ctx) {
    }

    @Override
    public void visitTerminal(@NotNull TerminalNode node) {
    }

    @Override
    public void visitErrorNode(@NotNull ErrorNode node) {
    }

    @Override
    public void enterEveryRule(@NotNull ParserRuleContext ctx) {
    }

    @Override
    public void exitEveryRule(@NotNull ParserRuleContext ctx) {
    }
}
