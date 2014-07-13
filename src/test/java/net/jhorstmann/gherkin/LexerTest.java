package net.jhorstmann.gherkin;

import net.jhorstmann.gherkin.antlr.GherkinLexer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LexerTest {
    @Test
    public void shouldLexScenarioOutline() {
        GherkinLexer lexer = new GherkinLexer(new ANTLRInputStream("Scenario Outline: ABC\n"));
        {
            Token keyword = lexer.nextToken();
            assertEquals("Scenario Outline", keyword.getText());
        }
        {
            Token ch = lexer.nextToken();
            assertEquals(":", ch.getText());
        }
        {
            Token ch = lexer.nextToken();
            assertEquals(" ", ch.getText());
        }
        {
            Token ch = lexer.nextToken();
            assertEquals("ABC", ch.getText());
        }
    }

    @Test
    public void shouldLexExamples() {
        GherkinLexer lexer = new GherkinLexer(new ANTLRInputStream("Examples:\n"));
        {
            Token keyword = lexer.nextToken();
            assertEquals("Examples", keyword.getText());
        }
        {
            Token ch = lexer.nextToken();
            assertEquals(":", ch.getText());
        }
    }

    @Test
    public void shouldLexKeywordsOnlyAtBeginning() {
        GherkinLexer lexer = new GherkinLexer(new ANTLRInputStream("Feature: Given\n"));
        {
            Token keyword = lexer.nextToken();
            assertEquals("Feature", keyword.getText());
        }
        {
            Token keyword = lexer.nextToken();
            assertEquals(":", keyword.getText());
        }
        {
            Token keyword = lexer.nextToken();
            assertEquals(" ", keyword.getText());
        }
        {
            Token keyword = lexer.nextToken();
            assertEquals("Given", keyword.getText());
        }
    }

}
