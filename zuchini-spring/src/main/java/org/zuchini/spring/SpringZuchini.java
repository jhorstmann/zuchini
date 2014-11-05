package org.zuchini.spring;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.Statement;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks;
import org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks;
import org.zuchini.junit.ZuchiniRunnerDelegate;

import java.util.Collections;
import java.util.List;

public class SpringZuchini extends ParentRunner<ZuchiniRunnerDelegate> {
    private final TestContextManager testContextManager;
    private final ZuchiniRunnerDelegate delegate;

    public SpringZuchini(Class<?> testClass) throws Exception {
        super(testClass);
        BeanFactoryScope globalScope = new BeanFactoryScope(false);
        BeanFactoryScope scenarioScope = new BeanFactoryScope(true);
        this.testContextManager = new TestContextManager(testClass);
        testContextManager.registerTestExecutionListeners(new ScopeExecutionListener(globalScope, scenarioScope));
        this.delegate = new ZuchiniRunnerDelegate(testClass, globalScope, scenarioScope);
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
    protected List<ZuchiniRunnerDelegate> getChildren() {
        return Collections.singletonList(delegate);
    }

    @Override
    protected Description describeChild(ZuchiniRunnerDelegate child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(ZuchiniRunnerDelegate child, RunNotifier notifier) {
        child.run(notifier);
    }

}
