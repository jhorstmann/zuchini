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
            assertEquals("Scenario Outline:", keyword.getText().trim());
        }
        {
            Token ch = lexer.nextToken();
            assertEquals("A", ch.getText());
        }
        {
            Token ch = lexer.nextToken();
            assertEquals("B", ch.getText());
        }
        {
            Token ch = lexer.nextToken();
            assertEquals("C", ch.getText());
        }
    }

    @Test
    public void shouldLexExamples() {
        GherkinLexer lexer = new GherkinLexer(new ANTLRInputStream("Examples:\n"));
        {
            Token keyword = lexer.nextToken();
            assertEquals("Examples:", keyword.getText().trim());
        }
    }

}
