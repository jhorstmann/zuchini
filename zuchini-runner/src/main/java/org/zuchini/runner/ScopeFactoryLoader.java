package org.zuchini.runner;

import java.util.Iterator;
import java.util.ServiceLoader;

class ScopeFactoryLoader {
    private ScopeFactoryLoader() {

    }

    static ScopeFactory load(ClassLoader cl) {
        ServiceLoader<ScopeFactory> serviceLoader = ServiceLoader.load(ScopeFactory.class, cl);
        Iterator<ScopeFactory> iterator = serviceLoader.iterator();
        ScopeFactory scopeFactory;
        if (iterator.hasNext()) {
            scopeFactory = iterator.next();
            if (iterator.hasNext()) {
                ScopeFactory secondScopeFactory = iterator.next();
                throw new IllegalStateException("More than one implementation of ScopeFactory found on classpath [" +
                        scopeFactory.getClass().getName() + "] and [" + secondScopeFactory.getClass().getName() + "]");
            }
            return scopeFactory;
        }
        return new DefaultScopeFactory();
    }
}
