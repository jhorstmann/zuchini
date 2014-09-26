package org.zuchini.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.junit.Test;
import org.zuchini.gherkin.antlr.GherkinParser;

import static org.junit.Assert.assertEquals;

public class ParserRulesTest {
    @Test
    public void shouldParseTables() {
        GherkinParser parser = FeatureParser.newParser(new ANTLRInputStream("|A|B|\n|1|2|\n"));
        GherkinParser.RowContext header = parser.row();
        assertEquals("A", header.cell(0).getText());
        assertEquals("B", header.cell(1).getText());
        GherkinParser.RowContext row = parser.row();
        assertEquals("1", row.cell(0).getText());
        assertEquals("2", row.cell(1).getText());
    }
}
