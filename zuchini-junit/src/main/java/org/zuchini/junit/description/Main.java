package org.zuchini.junit.description;

import org.zuchini.model.Feature;
import org.zuchini.model.Scenario;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Feature feature = new Feature("uri", 1, "Feature", "I can convert a feature to an annotation");
        feature.getComments().add("comment");
        feature.getTags().add("tag");

        Scenario scenario = new Scenario(feature, 2, "Scenario", "I can convert a scenario to an annotation");
        feature.getScenarios().add(scenario);

        FeatureInfo info = AnnotationHandler.create(FeatureInfo.class, feature);


        System.out.println(info.uri());
        System.out.println(info.lineNumber());
        System.out.println(info.keyword());
        System.out.println(info.description());
        System.out.println(Arrays.toString(info.comments()));
        System.out.println(Arrays.toString(info.tags()));

        System.out.println();
        ScenarioInfo scenarioInfo = info.scenarios()[0];

        System.out.println(scenarioInfo.lineNumber());
        System.out.println(scenarioInfo.keyword());
        System.out.println(scenarioInfo.description());
    }
}
