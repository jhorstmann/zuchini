package org.zuchini.spring;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.zuchini.runner.GlobalScope;
import org.zuchini.runner.Scope;

class BeanFactoryScope implements Scope {
    private boolean clearThreadLocalScope;
    private BeanFactory beanFactory;
    private GlobalScope fallback;

    BeanFactoryScope(boolean clearThreadLocalScope) {
        this.clearThreadLocalScope = clearThreadLocalScope;
        this.fallback = new GlobalScope();
    }

    void setBeanFactory(BeanFactory beanFactory) {
        if (this.beanFactory != null) {
            throw new IllegalStateException("BeanFactory can only be set once");
        }
        this.beanFactory = beanFactory;
    }

    @Override
    public void begin() {
        if (beanFactory == null) {
            throw new IllegalStateException("BeanFactory is not set");
        }
        fallback.begin();
    }

    @Override
    public <T> T getObject(Class<T> clazz) {
        if (fallback.contains(clazz)) {
            return fallback.getObject(clazz);
        }
        try {
            return beanFactory.getBean(clazz);
        } catch (NoSuchBeanDefinitionException ex) {
            return fallback.getObject(clazz);
        }
    }

    @Override
    public void end() {
        if (clearThreadLocalScope) {
            try {
                SpringThreadLocalScope threadLocalScope = beanFactory.getBean(SpringThreadLocalScope.class);
                threadLocalScope.clear();
            } catch (NoSuchBeanDefinitionException ex) {
                // ignore
            }
        }
        fallback.end();
    }
}
