package org.zuchini.spring;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.Statement;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks;
import org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks;
import org.zuchini.junit.FeatureRunner;
import org.zuchini.junit.ZuchiniOptions;
import org.zuchini.junit.ZuchiniRunnerDelegate;

import java.util.List;

public class SpringZuchini extends ParentRunner<FeatureRunner> {
    private final TestContextManager testContextManager;
    private final ZuchiniRunnerDelegate delegate;

    public SpringZuchini(Class<?> testClass) throws Exception {
        super(testClass);
        ZuchiniOptions options = testClass.getAnnotation(ZuchiniOptions.class);
        BeanFactoryScope globalScope = new BeanFactoryScope(false);
        BeanFactoryScope scenarioScope = new BeanFactoryScope(true);
        this.testContextManager = new TestContextManager(testClass);
        testContextManager.registerTestExecutionListeners(new ScopeExecutionListener(globalScope, scenarioScope));
        this.delegate = new ZuchiniRunnerDelegate(testClass, options, scenarioScope);
    }

    @Override
    protected Statement withBeforeClasses(Statement statement) {
        Statement junitBeforeClasses = super.withBeforeClasses(statement);
        return new RunBeforeTestClassCallbacks(junitBeforeClasses, testContextManager);
    }

    @Override
    protected Statement withAfterClasses(Statement statement) {
        Statement junitAfterClasses = super.withAfterClasses(statement);
        return new RunAfterTestClassCallbacks(junitAfterClasses, testContextManager);
    }

    @Override
    protected List<FeatureRunner> getChildren() {
        return delegate.getChildren();
    }

    @Override
    protected Description describeChild(FeatureRunner child) {
        return delegate.describeChild(child);
    }

    @Override
    protected void runChild(FeatureRunner child, RunNotifier notifier) {
        delegate.runChild(child, notifier);
    }

}
