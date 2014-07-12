package net.jhorstmann.gherkin;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import net.jhorstmann.gherkin.antlr.GherkinLexer;
import net.jhorstmann.gherkin.antlr.GherkinParser;
import net.jhorstmann.gherkin.model.Feature;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.*;

public class FeatureParser {

    public static Feature getFeature(File file) throws IOException {
        ANTLRInputStream inputStream = new ANTLRInputStream(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
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
        GherkinParser.FeatureContext featureContext = newParser(inputStream).feature();

        ParseTreeWalker walker = new ParseTreeWalker();
        FeatureWalker listener = new FeatureWalker(inputStream.getSourceName());
        walker.walk(listener, featureContext);

        return listener.getFeature();
    }

    @VisibleForTesting
    static GherkinParser newParser(ANTLRInputStream inputStream) {
        GherkinLexer lexer = new GherkinLexer(inputStream);
        return new GherkinParser(new BufferedTokenStream(lexer));
    }
}
