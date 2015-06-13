package org.zuchini.reporter;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.util.concurrent.AtomicLongMap;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.zuchini.junit.description.FeatureInfo;
import org.zuchini.junit.description.RowInfo;
import org.zuchini.junit.description.ScenarioInfo;
import org.zuchini.junit.description.StepInfo;

import javax.xml.bind.DatatypeConverter;
import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Multimaps.index;

public class JsonReporter extends RunListener {

    enum IndexByFeature implements Function<ScenarioResult, FeatureInfo> {
        INSTANCE;

        @Override
        public FeatureInfo apply(final ScenarioResult input) {
            final Description description = input.getDescription();
            return checkNotNull(description.getAnnotation(FeatureInfo.class));
        }
    }

    enum IndexByScenario implements Function<ScenarioResult, ScenarioInfo> {
        INSTANCE;

        @Override
        public ScenarioInfo apply(final ScenarioResult input) {
            final Description description = input.getDescription();
            return checkNotNull(description.getAnnotation(ScenarioInfo.class));
        }

    }

    private static IndexByFeature byFeature() {
        return IndexByFeature.INSTANCE;
    }

    private static IndexByScenario byScenario() {
        return IndexByScenario.INSTANCE;
    }

    private final AtomicLongMap<String> generatedIds = AtomicLongMap.create();
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
        final ImmutableListMultimap<FeatureInfo, ScenarioResult> features = index(copyResults(), byFeature());

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
            json.writeStringField("creation-time", DatatypeConverter.printDateTime(new GregorianCalendar()));

            writeEnvironment(json);

            json.writeArrayFieldStart("features");

            for (Map.Entry<FeatureInfo, Collection<ScenarioResult>> featureEntry : features.asMap().entrySet()) {
                final FeatureInfo feature = featureEntry.getKey();
                final Collection<ScenarioResult> value = featureEntry.getValue();
                // TODO: distinguish feature title and user story
                final String name = feature.description();

                json.writeStartObject();

                json.writeStringField("id", generateId(name));
                json.writeStringField("uri", feature.uri());
                json.writeNumberField("line", feature.lineNumber());

                writeStringArray(json, "tags", feature.tags());
                writeStringArray(json, "comments", feature.tags());

                json.writeStringField("keyword", feature.keyword());
                json.writeStringField("title", name);
                json.writeStringField("description", feature.description());

                writeScenarios(json, value, generatedIds);

                json.writeEndObject();
            }

            json.writeEndArray(); // features

            json.writeEndObject();
        }

    }

    private String generateId(final String title) {
        final String id = title.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        final long count = generatedIds.addAndGet(id, 1L);
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
                                final AtomicLongMap<String> generatedIds) throws IOException {
        final ImmutableListMultimap<ScenarioInfo, ScenarioResult> scenarios = index(value, byScenario());

        json.writeArrayFieldStart("scenarios");
        for (Map.Entry<ScenarioInfo, Collection<ScenarioResult>> scenarioEntry
                : scenarios.asMap().entrySet()) {
            final ScenarioInfo scenario = scenarioEntry.getKey();
            final String description = scenario.description();

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
        json.writeStringField("title", step.description());

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

            final Optional<Throwable> optionalException = result.getException();
            if (optionalException.isPresent()) {
                final Throwable exception = optionalException.get();

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
