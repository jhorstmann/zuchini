package org.zuchini.runner.tables;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class NamingConventionTest {

    @Parameterized.Parameters(name = "{0}: {1}.{2}")
    public static Collection<Object[]> parameters() {
        return asList(new Object[][] {
                {NamingConventions.DefaultNamingConventions.TITLECASE, ExampleBean.class, "width", "Width"},
                {NamingConventions.DefaultNamingConventions.TITLECASE, ExampleBean.class, "longDescription", "Long Description"},
                {NamingConventions.DefaultNamingConventions.DISPLAY_NAME, ExampleBean.class, "width", "width"},
                {NamingConventions.DefaultNamingConventions.DISPLAY_NAME, ExampleBean.class, "longDescription", "Long Description"}
        });
    }

    private final NamingConvention namingConvention;
    private final Class<?> beanClass;
    private final String property;
    private final String displayName;

    public NamingConventionTest(NamingConvention namingConvention, Class<?> beanClass, String property, String displayName) {
        this.namingConvention = namingConvention;
        this.beanClass = beanClass;
        this.property = property;
        this.displayName = displayName;
    }

    @Test
    public void test() {
        assertEquals(displayName, namingConvention.toDisplayName(beanClass, property));
        assertEquals(property, namingConvention.toProperty(beanClass, displayName));
    }

    @Test
    public void roundtrip() {
        assertEquals(displayName, namingConvention.toDisplayName(beanClass, namingConvention.toProperty(beanClass, displayName)));
        assertEquals(property, namingConvention.toProperty(beanClass, namingConvention.toDisplayName(beanClass, property)));
    }
}
