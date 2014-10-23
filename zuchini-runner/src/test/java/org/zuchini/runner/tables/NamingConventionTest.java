package org.zuchini.runner.tables;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class NamingConventionTest {

    private final NamingConvention namingConvention;
    private final String property;
    private final String displayName;

    public NamingConventionTest(NamingConvention namingConvention, String property, String displayName) {
        this.namingConvention = namingConvention;
        this.property = property;
        this.displayName = displayName;
    }

    @Parameterized.Parameters(name = "{0}: {1} <-> {2}")
    public static Collection<Object[]> parameters() {
        return asList(new Object[][]{
                {NamingConventions.DefaultNamingConventions.TITLECASE, "width", "Width"},
                {NamingConventions.DefaultNamingConventions.TITLECASE, "longDescription", "Long Description"},
                {new BeanInfoNamingConvention(ExampleBean.class), "width", "width"},
                {new BeanInfoNamingConvention(ExampleBean.class), "longDescription", "Long Description Display Name"}
        });
    }

    @Test
    public void test() {
        assertEquals(displayName, namingConvention.toDisplayName(property));
        assertEquals(property, namingConvention.toProperty(displayName));
    }

    @Test
    public void roundtrip() {
        assertEquals(displayName, namingConvention.toDisplayName(namingConvention.toProperty(displayName)));
        assertEquals(property, namingConvention.toProperty(namingConvention.toDisplayName(property)));
    }
}
