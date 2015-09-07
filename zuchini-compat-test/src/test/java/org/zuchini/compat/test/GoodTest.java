package org.zuchini.compat.test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import gherkin.ast.FeatureNode;
import org.json.JSONException;
import org.junit.AssumptionViolatedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.skyscreamer.jsonassert.JSONAssert;
import org.zuchini.compat.ast.AstBuilder;
import org.zuchini.model.Feature;
import org.zuchini.parser.FeatureParser;
import org.zuchini.runner.internal.ClasspathScanner;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@RunWith(Parameterized.class)
public class GoodTest {

    static final Set<String> EXCLUDE = new HashSet<>(asList("language.feature", "spaces_in_language.feature", "i18n_no.feature", "descriptions.feature", "docstrings.feature"));

    static class FeatureAndAst {

        final URL feature;
        final URL ast;

        public FeatureAndAst(URL feature, URL ast) {
            this.feature = feature;
            this.ast = ast;
        }

        public String toString() {
            final String file = feature.getFile();
            final int i = file.lastIndexOf("/");
            return i >= 0 ? file.substring(i+1) : file;
        }

    }
    static class AstScanner extends ClasspathScanner {

        private final List<FeatureAndAst> features;
        protected AstScanner() {
            super(GoodTest.class.getClassLoader(), asList("testdata/good"), ".feature");

            features = new ArrayList<>();
        }

        @Override
        protected void handleResource(String resourceName) throws IOException {
            final URL feature = classLoader.getResource(resourceName);
            final URL ast = classLoader.getResource(resourceName + ".ast.json");
            features.add(new FeatureAndAst(feature, ast));

        }

        static List<FeatureAndAst> getFeatures() throws IOException {
            final AstScanner scanner = new AstScanner();
            scanner.scan();
            return scanner.features;
        }

    }
    private static ObjectMapper createMapper() {
        final ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new ParameterNamesModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return mapper;
    }

    private static final ObjectMapper mapper = createMapper();

    private final FeatureAndAst featureAndAst;

    public GoodTest(FeatureAndAst featureAndAst) {
        this.featureAndAst = featureAndAst;
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> parameters() throws IOException {
        final List<FeatureAndAst> features = AstScanner.getFeatures();
        return features.stream().map(o -> new Object[]{o}).collect(toList());
    }

    private void checkExclusion() {
        String file = featureAndAst.feature.getFile();

        final int idx = file.lastIndexOf('/');
        if (idx >= 0) {
            file = file.substring(idx+1);
        }

        if (EXCLUDE.contains(file)) {
            throw new AssumptionViolatedException("Feature " + file + " is not supported");
        }
    }

    @Test
    public void compare() throws JSONException, IOException {

        checkExclusion();

        final FeatureNode actual = AstBuilder.toFeatureNode(FeatureParser.getFeature(featureAndAst.feature));
        final FeatureNode expected = mapper.readValue(featureAndAst.ast, FeatureNode.class);

        final String actualString = mapper.writeValueAsString(actual);
        final String expectedString = mapper.writeValueAsString(expected);

        JSONAssert.assertEquals(expectedString, actualString, true);
    }
}
