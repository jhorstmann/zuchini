package org.zuchini.reporter;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.zuchini.junit.description.FeatureInfo;
import org.zuchini.junit.description.RowInfo;
import org.zuchini.junit.description.ScenarioInfo;
import org.zuchini.junit.description.StepInfo;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class JsonReporter extends RunListener {

    private static <K, V> void update(Map<K, List<V>> map, K key, V val) {
        List<V> list = map.get(key);
        if (list == null) {
            list = new ArrayList<>();
            map.put(key, list);
        }
        list.add(val);
    }

    private static <T extends Annotation> Map<T, List<ScenarioResult>> index(Iterable<ScenarioResult> results, Class<T> clazz) {
        final Map<T, List<ScenarioResult>> index = new HashMap<>();

        for (ScenarioResult result : results) {
            final Description description = result.getDescription();
            final T scenario = description.getAnnotation(clazz);

            if (scenario == null) {
                throw new IllegalStateException("Description [" + description.getDisplayName() + "] has no annotation of type [" + clazz.getName() + "]");
            }

            update(index, scenario, result);
        }

        return index;
    }

    private static Map<FeatureInfo, List<ScenarioResult>> byFeature(Iterable<ScenarioResult> results) {
        return index(results, FeatureInfo.class);
    }

    private static Map<ScenarioInfo, List<ScenarioResult>> byScenario(Iterable<ScenarioResult> results) {
        return index(results, ScenarioInfo.class);
    }


    private final Map<String, Integer> generatedIds = new HashMap<>();
    private final List<ScenarioResult> results = new ArrayList<>();
    private final Set<Description> failed = Collections.newSetFromMap(new IdentityHashMap<Description, Boolean>());
    private final Object lock = new Object();

    @Override
    public void testStarted(Description description) throws Exception {
    }


    @Override
    public void testFinished(Description description) throws Exception {
        synchronized (lock) {
            if (!failed.contains(description)) {
                results.add(ScenarioResult.success(description));
            }
        }
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        synchronized (lock) {
            failed.add(failure.getDescription());
            results.add(ScenarioResult.failure(failure));
        }
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        synchronized (lock) {
            failed.add(failure.getDescription());
            results.add(ScenarioResult.assumptionFailed(failure));
        }
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        synchronized (lock) {
            failed.add(description);
            results.add(ScenarioResult.ignored(description));
        }
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
    }

    private List<ScenarioResult> copyResults() {
        synchronized (lock) {
            return new ArrayList<>(results);
        }
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        Map<FeatureInfo, List<ScenarioResult>> features = byFeature(copyResults());

        String outputPath = System.getProperty("zuchini.reporter.output");
        if (outputPath == null) {
            outputPath = "./zuchini-report.json";
        }

        final File outputFile = new File(outputPath).getAbsoluteFile();

        outputFile.getParentFile().mkdirs();

        final JsonFactory jsonFactory = new JsonFactory();

        try (JsonGenerator json = jsonFactory.createGenerator(outputFile, JsonEncoding.UTF8)) {
            json.useDefaultPrettyPrinter();

            json.writeStartObject();
            json.writeStringField("creation-host", InetAddress.getLocalHost().getHostName());
            json.writeStringField("creation-time", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date()));

            writeEnvironment(json);

            json.writeArrayFieldStart("features");

            for (Map.Entry<FeatureInfo, List<ScenarioResult>> featureEntry : features.entrySet()) {
                final FeatureInfo feature = featureEntry.getKey();
                final Collection<ScenarioResult> value = featureEntry.getValue();
                // TODO: distinguish feature title and user story
                final String name = feature.name();

                json.writeStartObject();

                json.writeStringField("id", generateId(name));
                json.writeStringField("uri", feature.uri());
                json.writeNumberField("line", feature.lineNumber());

                writeStringArray(json, "tags", feature.tags());
                writeStringArray(json, "comments", feature.tags());

                json.writeStringField("keyword", feature.keyword());
                json.writeStringField("title", name);
                json.writeStringField("name", feature.name());

                writeScenarios(json, value, generatedIds);

                json.writeEndObject();
            }

            json.writeEndArray(); // features

            json.writeEndObject();
        }

    }

    private String generateId(final String title) {
        final String id = title.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        Integer count = generatedIds.get(id);
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        generatedIds.put(id, count);
        return count == 1 ? id : (id + "-" + count);
    }

    private void writeStringArray(JsonGenerator json, String name, String[] strings) throws IOException {
        if (strings != null && strings.length > 0) {
            json.writeArrayFieldStart(name);
            for (String tag : strings) {
                json.writeString(tag);
            }

            json.writeEndArray();
        }
    }

    private void writeEnvironment(final JsonGenerator json) throws IOException {
        // TODO: Are system properties and environment variables actually useful or an information leak?

        final Properties properties = new Properties();
        if (!properties.isEmpty()) {
            json.writeObjectFieldStart("properties");
            for (String key : properties.stringPropertyNames()) {
                final String val = properties.getProperty(key);
                json.writeStringField(key, val);
            }
            json.writeEndObject();
        }

        final Map<String, String> env = new HashMap<>();
        if (!env.isEmpty()) {
            json.writeObjectFieldStart("environment");
            for (Map.Entry<String, String> entry : env.entrySet()) {
                json.writeStringField(entry.getKey(), entry.getValue());
            }
            json.writeEndObject();
        }


    }

    private void writeScenarios(final JsonGenerator json, final Collection<ScenarioResult> value,
                                final Map<String, Integer> generatedIds) throws IOException {
        Map<ScenarioInfo, List<ScenarioResult>> scenarios = byScenario(results);

        json.writeArrayFieldStart("scenarios");
        for (Map.Entry<ScenarioInfo, List<ScenarioResult>> scenarioEntry
                : scenarios.entrySet()) {
            final ScenarioInfo scenario = scenarioEntry.getKey();
            final String description = scenario.name();

            json.writeStartObject();

            json.writeNumberField("line", scenario.lineNumber());

            writeStringArray(json, "tags", scenario.tags());

            writeStringArray(json, "comments", scenario.comments());

            json.writeStringField("id", generateId(description));
            json.writeStringField("keyword", scenario.keyword());
            json.writeStringField("title", description);

            writeSteps(json, scenario);

            writeResults(json, scenarioEntry.getValue());

            json.writeEndObject(); // scenario
        }

        json.writeEndArray(); // scenarios
    }

    private void writeSteps(final JsonGenerator json, final ScenarioInfo scenario) throws IOException {
        json.writeArrayFieldStart("steps");

        for (StepInfo step : scenario.steps()) {

            json.writeStartObject();
            writeStep(json, step);
            json.writeEndObject();
        }

        json.writeEndArray();

    }

    private void writeStep(JsonGenerator json, StepInfo step) throws IOException {
        json.writeNumberField("line", step.lineNumber());

        json.writeStringField("keyword", step.keyword());
        json.writeStringField("title", step.name());

        writeStringArray(json, "tags", step.tags());
        writeStringArray(json, "comments", step.comments());
        writeStringArray(json, "documents", step.docs());

        writeDatatable(json, step);

    }

    private void writeDatatable(final JsonGenerator json, final StepInfo step) throws IOException {
        if (step.rows() != null && step.rows().length > 0) {
            json.writeObjectFieldStart("datatable");

            json.writeArrayFieldStart("rows");

            for (RowInfo row : step.rows()) {
                json.writeStartObject();
                json.writeNumberField("line", row.lineNumber());

                writeStringArray(json, "comments", row.comments());
                writeStringArray(json, "tags", row.tags());
                writeStringArray(json, "cells", row.cells());

                json.writeEndObject();
            }

            json.writeEndArray();

            json.writeEndObject();
        }
    }

    private static String getStackTrace(Throwable exception) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);

        return sw.toString();
    }

    private void writeResults(final JsonGenerator json, final Collection<ScenarioResult> value) throws IOException {
        json.writeArrayFieldStart("results");
        for (ScenarioResult result : value) {
            json.writeStartObject();

            StepInfo step = result.getDescription().getAnnotation(StepInfo.class);
            if (step != null) {
                // if reporting individual steps there will be multiple results
                json.writeObjectFieldStart("step");
                writeStep(json, step);
                json.writeEndObject();
            }

            json.writeBooleanField("success", result.isSuccess());
            json.writeBooleanField("ignored", result.isIgnored());
            json.writeBooleanField("assumptionFailed", result.isAssumptionFailed());

            Throwable exception = result.getException();
            if (exception != null) {
                json.writeStringField("error", exception.getMessage());
                json.writeStringField("stacktrace", getStackTrace(exception));
            }

            writeParameters(json, result);
            writeMetadata(json, result);

            json.writeEndObject(); // result
        }

        json.writeEndArray(); // results
    }

    private void writeParameters(final JsonGenerator json, final ScenarioResult results) throws IOException {
        // TODO: no parameterized execution yet
        final Map<Class<?>, Object> parameters = new HashMap<>();
        if (!parameters.isEmpty()) {
            json.writeObjectFieldStart("parameters");
            for (Map.Entry<Class<?>, Object> parameter : parameters.entrySet()) {
                final String name = Introspector.decapitalize(parameter.getKey().getSimpleName());
                final Object value = parameter.getValue();

                writeProperty(json, name, value);
            }

            json.writeEndObject();
        }
    }

    private void writeMetadata(final JsonGenerator json, final ScenarioResult results) throws IOException {
        // TODO: no support for adding metadata to results yet
        final Map<String, Object> metadata = new HashMap<>();
        if (!metadata.isEmpty()) {
            json.writeObjectFieldStart("metadata");
            for (Map.Entry<String, Object> property : metadata.entrySet()) {
                writeProperty(json, property.getKey(), property.getValue());
            }

            json.writeEndObject();
        }
    }

    private void writeProperty(final JsonGenerator json, final String name, final Object value) throws IOException {
        json.writeFieldName(name);

        if (value == null) {
            json.writeNull();
        } else if (value instanceof Number) {
            json.writeNumber(((Number) value).doubleValue());
        } else {
            json.writeString(String.valueOf(value));
        }
    }

}
