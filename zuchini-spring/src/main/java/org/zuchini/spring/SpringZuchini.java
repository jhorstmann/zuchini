package org.zuchini.spring;

import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks;
import org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks;
import org.zuchini.junit.ZuchiniRunnerDelegate;

import java.io.IOException;
import java.util.Collections;

public class SpringZuchini extends Suite {
    private final TestContextManager testContextManager;

    public SpringZuchini(Class<?> testClass) throws Exception {
        this(testClass, new ScopeExecutionListener());
    }

    private SpringZuchini(Class<?> testClass, ScopeExecutionListener scopeExecutionListener) throws Exception {
        super(testClass, Collections.<Runner>singletonList(delegate(testClass, scopeExecutionListener)));

        this.testContextManager = new TestContextManager(testClass);
        this.testContextManager.registerTestExecutionListeners(scopeExecutionListener);
    }

    private static ZuchiniRunnerDelegate delegate(Class<?> testClass, ScopeExecutionListener scopeExecutionListener) throws InitializationError, IOException {
        return new ZuchiniRunnerDelegate(testClass,
                scopeExecutionListener.getGlobalScope(),
                scopeExecutionListener.getScenarioScope());
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


}
