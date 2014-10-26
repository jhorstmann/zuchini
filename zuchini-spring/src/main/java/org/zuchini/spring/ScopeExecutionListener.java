package org.zuchini.spring;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

class ScopeExecutionListener implements TestExecutionListener {
    private final BeanFactoryScope globalScope;
    private final BeanFactoryScope scenarioScope;

    ScopeExecutionListener(BeanFactoryScope globalScope, BeanFactoryScope scenarioScope) {
        this.globalScope = globalScope;
        this.scenarioScope = scenarioScope;
    }

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        ApplicationContext applicationContext = testContext.getApplicationContext();
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        globalScope.setBeanFactory(beanFactory);
        scenarioScope.setBeanFactory(beanFactory);
        globalScope.begin();
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        globalScope.end();
    }

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
    }
}
