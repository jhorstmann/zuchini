package org.zuchini.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.zuchini.gherkin.antlr.GherkinLexer;
import org.zuchini.gherkin.antlr.GherkinParser;
import org.zuchini.model.Feature;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class FeatureParser {

    private static final String DEFAULT_CHARSET = "utf-8";

    private FeatureParser() {
    }

    public static Feature getFeature(File file) throws IOException {
        ANTLRInputStream inputStream = new ANTLRInputStream(new InputStreamReader(new FileInputStream(file),
                DEFAULT_CHARSET));
        inputStream.name = file.toURI().toASCIIString();
        return getFeature(inputStream);
    }

    public static Feature getFeature(Reader reader) throws IOException {
        return getFeature(new ANTLRInputStream(reader));
    }

    public static Feature getFeature(String input) {
        return getFeature(new ANTLRInputStream(input));
    }

    public static Feature getFeature(ANTLRInputStream inputStream) {
        return parseWithVisitor(inputStream);
    }

    static Feature parseWithWalker(ANTLRInputStream inputStream) {
        GherkinParser.FeatureContext featureContext = newParser(inputStream).feature();

        ParseTreeWalker walker = new ParseTreeWalker();
        FeatureWalker listener = new FeatureWalker(inputStream.getSourceName());
        walker.walk(listener, featureContext);

        return listener.getFeature();
    }

    static Feature parseWithVisitor(ANTLRInputStream inputStream) {
        return newParser(inputStream).feature().accept(new Visitors.FeatureVisitor(inputStream.getSourceName()));
    }

    static GherkinParser newParser(ANTLRInputStream inputStream) {
        GherkinLexer lexer = new GherkinLexer(inputStream);
        GherkinParser parser = new GherkinParser(new BufferedTokenStream(lexer));
        return parser;
    }
}
