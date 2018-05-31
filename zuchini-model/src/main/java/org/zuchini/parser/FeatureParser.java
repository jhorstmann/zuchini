package org.zuchini.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.zuchini.gherkin.antlr.GherkinLexer;
import org.zuchini.gherkin.antlr.GherkinParser;
import org.zuchini.model.Feature;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

public class FeatureParser {

    private static final String DEFAULT_CHARSET = "utf-8";

    private FeatureParser() {
    }

    public static Feature getFeature(URL url) throws IOException {
        try (final InputStream in = url.openStream()) {
            return getFeature(url.toExternalForm(), in);
        }
    }

    public static Feature getFeature(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        return getFeature(file.toURI().toASCIIString(), inputStream);
    }

    public static Feature getFeature(String uri, InputStream inputStream) throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new InputStreamReader(inputStream, DEFAULT_CHARSET));
        input.name = uri;
        return getFeature(input);
    }

    public static Feature getFeature(String uri, Reader reader) throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(reader);
        input.name = uri;
        return getFeature(input);
    }

    public static Feature getFeature(Reader reader) throws IOException {
        return getFeature(new ANTLRInputStream(reader));
    }

    public static Feature getFeature(String input) {
        return getFeature(new ANTLRInputStream(appendNewLineAtEOFIfMissing(input)));
    }

    private static String appendNewLineAtEOFIfMissing(String input) {
        final String newLine = System.lineSeparator();
        if (!input.endsWith(newLine)) {
            input = input + newLine;
        }
        return input;
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
        GherkinParser.FeatureContext feature = newParser(inputStream).feature();
        return feature.accept(new Visitors.FeatureVisitor(inputStream.getSourceName()));
    }

    static GherkinParser newParser(ANTLRInputStream inputStream) {
        GherkinLexer lexer = new GherkinLexer(inputStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
        GherkinParser parser = new GherkinParser(new BufferedTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(ThrowingErrorListener.INSTANCE);
        return parser;
    }
}
