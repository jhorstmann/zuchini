package org.zuchini.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Outline extends StepContainer {
    private final List<Examples> examples = new ArrayList<>();

    public Outline(Feature feature, int lineNumber, String keyword, String description) {
        super(feature, lineNumber, keyword, description);
    }

    public List<Examples> getExamples() {
        return examples;
    }

    public List<Row> getExampleRows() {
        List<Row> rows = new ArrayList<>();
        for (Examples example : examples) {
            rows.addAll(example.getRows());
        }

        return rows;
    }

    private static String replaceExampleValues(String string, Pattern matchPattern, Map<String, String> exampleValues) {
        StringBuffer sb = new StringBuffer(string.length());
        Matcher matcher = matchPattern.matcher(string);
        while (matcher.find()) {
            String var = matcher.group(1);
            String value = exampleValues.get(var);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private Row buildRow(Pattern pattern, Row exampleStepData, Map<String, String> exampleValues) {
        Row row = new Row(getFeature(), exampleStepData.getLineNumber());
        row.getComments().addAll(exampleStepData.getComments());
        row.getTags().addAll(exampleStepData.getTags());
        for (String cell : exampleStepData.getCells()) {
            row.getCells().add(replaceExampleValues(cell, pattern, exampleValues));
        }
        return row;
    }

    private Scenario buildScenario(Pattern pattern, Row exampleRow, Map<String, String> exampleValues) {
        Scenario scenario = new Scenario(getFeature(), exampleRow.getLineNumber(), getKeyword(), getName(), exampleValues);
        for (Step exampleStep : getSteps()) {
            String stepDescription = replaceExampleValues(exampleStep.getName(), pattern, exampleValues);
            Step step = new Step(scenario, exampleStep.getLineNumber(), exampleStep.getKeyword(), stepDescription);
            step.getComments().addAll(exampleStep.getComments());
            step.getTags().addAll(exampleStep.getTags());

            for (String doc : exampleStep.getDocs()) {
                step.getDocs().add(replaceExampleValues(doc, pattern, exampleValues));
            }

            for (Row exampleStepData : exampleStep.getRows()) {
                step.getRows().add(buildRow(pattern, exampleStepData, exampleValues));
            }

            scenario.getSteps().add(step);
        }
        return scenario;
    }

    private static Pattern buildPattern(List<String> cells) {
        StringBuilder sb = new StringBuilder("<(");
        for (Iterator<String> it=cells.iterator(); it.hasNext();) {
            String cell = it.next();
            sb.append(Pattern.quote(cell));
            if (it.hasNext()) {
                sb.append("|");
            }
        }
        sb.append(")>");
        return Pattern.compile(sb.toString());
    }

    public List<Scenario> buildScenarios() {
        List<Scenario> result = new ArrayList<>(examples.size()-1);
        for (Examples example : examples) {
            result.addAll(buildScenarios(example));
        }

        return result;
    }

    private List<Scenario> buildScenarios(Examples examples) {
        List<Row> exampleRows = examples.getRows();

        List<Scenario> result = new ArrayList<>();
        List<String> header = exampleRows.get(0).getCells();
        Pattern pattern = buildPattern(header);
        for (Row exampleRow : exampleRows.subList(1, exampleRows.size())) {
            Map<String, String> exampleValues = Table.rowToMap(header, exampleRow);
            Scenario scenario = buildScenario(pattern, exampleRow, exampleValues);
            scenario.getComments().addAll(exampleRow.getComments());
            scenario.getTags().addAll(getTags());
            scenario.getTags().addAll(examples.getTags());
            scenario.getTags().addAll(exampleRow.getTags());

            result.add(scenario);
        }
        return result;
    }
}
