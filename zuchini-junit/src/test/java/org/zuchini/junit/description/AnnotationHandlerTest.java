package org.zuchini.junit.description;

import org.junit.Assert;
import org.junit.Test;
import org.zuchini.model.Feature;
import org.zuchini.model.Scenario;

import java.util.HashSet;
import java.util.Set;

public class AnnotationHandlerTest {
    private Feature createFeature() {
        Feature feature = new Feature("uri", 1, "Feature", "I can convert a feature to an annotation");
        feature.getComments().add("comment");
        feature.getTags().add("tag");

        Scenario scenario = new Scenario(feature, 2, "Scenario", "I can convert a scenario to an annotation");
        feature.getScenarios().add(scenario);
        return feature;
    }

    @Test
    public void testIdentityEquals() {
        Feature feature = createFeature();

        FeatureInfo featureInfo1 = AnnotationHandler.create(FeatureInfo.class, feature);
        FeatureInfo featureInfo2 = AnnotationHandler.create(FeatureInfo.class, feature);

        Assert.assertFalse(featureInfo1 == featureInfo2);
        Assert.assertEquals(featureInfo1, featureInfo2);
    }
    @Test
    public void testDeepEquals() {
        Feature feature1 = createFeature();
        Feature feature2 = createFeature();

        FeatureInfo featureInfo1 = AnnotationHandler.create(FeatureInfo.class, feature1);
        FeatureInfo featureInfo2 = AnnotationHandler.create(FeatureInfo.class, feature2);

        Assert.assertFalse(featureInfo1 == featureInfo2);
        Assert.assertEquals(featureInfo1, featureInfo2);
    }

    @Test
    public void testHashCode() {
        Feature feature1 = createFeature();
        Feature feature2 = createFeature();

        FeatureInfo featureInfo1 = AnnotationHandler.create(FeatureInfo.class, feature1);
        FeatureInfo featureInfo2 = AnnotationHandler.create(FeatureInfo.class, feature2);

        Set<FeatureInfo> set = new HashSet<>();
        set.add(featureInfo1);

        Assert.assertTrue(set.contains(featureInfo1));
        Assert.assertTrue(set.contains(featureInfo2));
    }

    @Test
    public void testToString() {
        Feature feature = createFeature();

        FeatureInfo featureInfo = AnnotationHandler.create(FeatureInfo.class, feature);

        String string = featureInfo.toString();
        Assert.assertTrue(string.startsWith("@" + FeatureInfo.class.getName() + "["));
        Assert.assertTrue(string.contains("description=I can convert a feature to an annotation"));
        Assert.assertTrue(string.contains("description=I can convert a scenario to an annotation"));
    }

}
