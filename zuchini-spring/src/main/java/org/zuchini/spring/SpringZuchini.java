package org.zuchini.spring;

import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.Statement;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks;
import org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks;
import org.zuchini.junit.ZuchiniRunnerDelegate;

import java.util.Collections;

public class SpringZuchini extends Suite {
    private final TestContextManager testContextManager;

    public SpringZuchini(Class<?> testClass) throws Exception {
        super(testClass, Collections.<Runner>singletonList(new ZuchiniRunnerDelegate(testClass, new BeanFactoryScope(false), new BeanFactoryScope(true))));
        
        // Hack because java does not allow any code before the super constructor call
        ZuchiniRunnerDelegate delegate = (ZuchiniRunnerDelegate) getChildren().get(0);
        BeanFactoryScope globalScope = (BeanFactoryScope) delegate.getGlobalScope();
        BeanFactoryScope scenarioScope = (BeanFactoryScope) delegate.getScenarioScope();

        this.testContextManager = new TestContextManager(testClass);
        this.testContextManager.registerTestExecutionListeners(new ScopeExecutionListener(globalScope, scenarioScope));
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
