package org.zuchini.spring.cukes.cukes;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static org.junit.Assert.assertNotNull;

@Component
public class SpringAutowiredRunListener extends RunListener {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void testRunStarted(Description description) throws Exception {
        assertNotNull("Autowiring should work in run listeners", applicationContext);
    }
}
