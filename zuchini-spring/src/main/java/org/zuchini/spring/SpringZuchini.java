package org.zuchini.spring;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.TestClass;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.TestExecutionListener;
import org.zuchini.junit.FeatureRunner;
import org.zuchini.junit.ZuchiniOptions;
import org.zuchini.junit.ZuchiniRunnerDelegate;
import org.zuchini.runner.World;
import org.zuchini.runner.WorldBuilder;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;

public class SpringZuchini extends ParentRunner<FeatureRunner> implements TestExecutionListener {
    private final ZuchiniOptions options;
    private final TestContextManager testContextManager;
    private final World world;
    private final ZuchiniRunnerDelegate delegate;
    private final BeanFactoryScope globalScope;
    private final BeanFactoryScope scenarioScope;

    public SpringZuchini(Class<?> testClass) throws Exception {
        super(testClass);
        this.options = testClass.getAnnotation(ZuchiniOptions.class);
        this.testContextManager = new TestContextManager(testClass);
        testContextManager.registerTestExecutionListeners(this);
        this.globalScope = new BeanFactoryScope(false);
        this.scenarioScope = new BeanFactoryScope(true);
        this.world = buildWorld(testClass, asList(options.featurePackages()), asList(options.stepDefinitionPackages()));
        this.delegate = new ZuchiniRunnerDelegate(testClass, world.getFeatureStatements(), scenarioScope,
                options.reportIndividualSteps());

    }

    private static World buildWorld(Class<?> testClass, List<String> featurePackages, List<String> stepDefinitionPackages) throws IOException {
        return new WorldBuilder(testClass.getClassLoader())
                .withDefaultConverterConfiguration()
                .withFeaturePackages(featurePackages)
                .withStepDefinitionPackages(stepDefinitionPackages)
                .buildWorld();
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            testContextManager.beforeTestClass();
            TestClass testClass = getTestClass();
            Object testInstance = testClass.getOnlyConstructor().newInstance();
            testContextManager.prepareTestInstance(testInstance);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            super.run(notifier);
        } finally {
            try {
                testContextManager.afterTestClass();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
