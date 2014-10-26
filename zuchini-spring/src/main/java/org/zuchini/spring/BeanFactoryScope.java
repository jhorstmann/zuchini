package org.zuchini.spring;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.zuchini.runner.Scope;

class BeanFactoryScope implements Scope, BeanFactoryAware {
    private boolean clearThreadLocalScope;
    private BeanFactory beanFactory;

    BeanFactoryScope(boolean clearThreadLocalScope) {
        this.clearThreadLocalScope = clearThreadLocalScope;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
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
    }

    @Override
    public <T> T getObject(Class<T> clazz) {
        // TODO: handle NoSuchBeanDefinitionException
        return beanFactory.getBean(clazz);
    }

    @Override
    public void end() {
        if (clearThreadLocalScope) {
            SpringThreadLocalScope threadLocalScope = beanFactory.getBean(SpringThreadLocalScope.class);
            threadLocalScope.clear();
        }
    }
}
