package org.zuchini.junit5;

import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.TestTag;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClasspathResourceSource;
import org.junit.platform.engine.support.descriptor.CompositeTestSource;
import org.junit.platform.engine.support.descriptor.FilePosition;
import org.zuchini.model.Feature;
import org.zuchini.model.LocationAware;
import org.zuchini.model.Named;
import org.zuchini.model.Outline;
import org.zuchini.model.Scenario;
import org.zuchini.model.Tagged;
import org.zuchini.runner.Context;
import org.zuchini.runner.FeatureStatement;
import org.zuchini.runner.OutlineStatement;
import org.zuchini.runner.ScenarioStatement;
import org.zuchini.runner.SimpleScenarioStatement;
import org.zuchini.runner.Statement;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

class ZuchiniTestDescriptor extends AbstractTestDescriptor {
    private final Set<TestTag> tags;
    private final Statement statement;
    private final Context context;

    private ZuchiniTestDescriptor(UniqueId uniqueId, String displayName, TestSource source, Set<TestTag> tags, @Nullable Statement statement, Context context) {
        super(uniqueId, displayName, source);
        this.tags = Collections.unmodifiableSet(new LinkedHashSet<>(tags));
        this.statement = statement;
        this.context = context;
    }

    private static String name(Named named) {
        return named.getKeyword() + " " + named.getName();
    }

    private static TestSource source(LocationAware location) {
        return ClasspathResourceSource.from(location.getUri(), FilePosition.from(location.getLineNumber()));
    }

    private static Set<TestTag> tags(Tagged tagged) {
        return tagged.getTags()
                .stream()
                .map(TestTag::create)
                .collect(toCollection(LinkedHashSet::new));
    }

    static ZuchiniTestDescriptor fromFeatures(UniqueId uniqueId, Context context, String displayName, List<FeatureStatement> featureStatements) {
        List<ZuchiniTestDescriptor> featureDescriptions = featureStatements.stream()
                .map(f -> fromFeature(uniqueId, context, f))
                .collect(toList());
        CompositeTestSource source = CompositeTestSource.from(featureDescriptions.stream()
                .map(f -> f.getSource().get())
                .collect(toList()));

        ZuchiniTestDescriptor desc = new ZuchiniTestDescriptor(uniqueId, displayName, source, Collections.emptySet(), null, context);
        for (ZuchiniTestDescriptor featureDescription : featureDescriptions) {
            desc.addChild(featureDescription);
        }

        return desc;
    }

    static ZuchiniTestDescriptor fromFeature(UniqueId rootId, Context context, FeatureStatement featureStatement) {
        Feature feature = featureStatement.getFeature();
        String featureName = name(feature);
        UniqueId featureId = rootId.append("feature", featureName);
        ZuchiniTestDescriptor desc = new ZuchiniTestDescriptor(featureId, featureName, source(feature), tags(feature), featureStatement, context);
        for (ScenarioStatement scenarioStatement : featureStatement.getScenarios()) {
            if (scenarioStatement instanceof SimpleScenarioStatement) {
                desc.addChild(fromScenario(featureId, context, (SimpleScenarioStatement) scenarioStatement));
            } else if (scenarioStatement instanceof OutlineStatement) {
                desc.addChild(fromOutline(featureId, context, (OutlineStatement) scenarioStatement));
            } else {
                throw new IllegalStateException("Unknown scenario type " + scenarioStatement.getClass().getName());
            }
        }

        return desc;
    }

    static ZuchiniTestDescriptor fromOutline(UniqueId featureId, Context context, OutlineStatement outlineStatement) {
        Outline outline = outlineStatement.getOutline();
        String outlineName = name(outline);
        UniqueId outlineId = featureId.append("outline", outlineName);
        ZuchiniTestDescriptor desc = new ZuchiniTestDescriptor(outlineId, outlineName, source(outline), tags(outline), outlineStatement, context);
        for (SimpleScenarioStatement simpleScenarioStatement : outlineStatement.getScenarios()) {
            desc.addChild(fromScenario(outlineId, context, simpleScenarioStatement));
        }

        return desc;
    }

    static ZuchiniTestDescriptor fromScenario(UniqueId parentId, Context context, SimpleScenarioStatement scenarioStatement) {
        Scenario scenario = scenarioStatement.getScenario();
        String scenarioName = name(scenario);
        UniqueId scenarioId = parentId.append("scenario", scenarioName);
        ZuchiniTestDescriptor desc = new ZuchiniTestDescriptor(scenarioId, scenarioName, source(scenario), tags(scenario), scenarioStatement, context);

        return desc;
    }

    @Override
    public Set<TestTag> getTags() {
        return tags;
    }



    @Override
    public Type getType() {
        return children.isEmpty() ? Type.TEST : Type.CONTAINER;
    }

    void run(EngineExecutionListener engineExecutionListener) {
        if (isTest()) {
            try {
                engineExecutionListener.executionStarted(this);
                statement.evaluate(context);
                engineExecutionListener.executionFinished(this, TestExecutionResult.successful());
            } catch (Throwable throwable) {
                engineExecutionListener.executionFinished(this, TestExecutionResult.failed(throwable));
            }
        } else if (isContainer()) {
            engineExecutionListener.executionStarted(this);
            try {
                for (TestDescriptor child : children) {
                    ((ZuchiniTestDescriptor) child).run(engineExecutionListener);
                }
            } finally {
                engineExecutionListener.executionFinished(this, TestExecutionResult.successful());
            }
        }
    }

}
