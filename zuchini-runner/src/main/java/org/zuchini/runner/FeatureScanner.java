package org.zuchini.runner;

import org.zuchini.model.Feature;
import org.zuchini.parser.FeatureParser;
import org.zuchini.runner.internal.ClasspathScanner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

class FeatureScanner extends ClasspathScanner {
    private List<Feature> features;
    private final String encoding;

    private FeatureScanner(ClassLoader classLoader, List<String> packageNames, String encoding) {
        super(classLoader, packageNames, ".feature");
        this.encoding = encoding;
        this.features = new ArrayList<>();
    }

    @Override
    protected void handleResource(String resourceName) throws IOException {

        try (InputStream in = classLoader.getResourceAsStream(resourceName)) {
            try (Reader reader = new InputStreamReader(in)) {
                Feature feature = FeatureParser.getFeature(resourceName, reader);
                features.add(feature);
            }
        }
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public static List<Feature> scan(ClassLoader classLoader, List<String> packageNames) throws IOException {
        FeatureScanner scanner = new FeatureScanner(classLoader, packageNames, "utf-8");
        scanner.scan();
        return scanner.getFeatures();

    }

    public static List<Feature> scan(ClassLoader classLoader, String... packageNames) throws IOException {
        return scan(classLoader, asList(packageNames));
    }

}
