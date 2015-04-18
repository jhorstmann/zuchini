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
                {NamingConventions.DefaultNamingConventions.UPPERCASE_WORDS, "width", "Width"},
                {NamingConventions.DefaultNamingConventions.UPPERCASE_WORDS, "longDescription", "Long Description"},
                {NamingConventions.DefaultNamingConventions.LOWERCASE_WORDS, "width", "width"},
                {NamingConventions.DefaultNamingConventions.LOWERCASE_WORDS, "longDescription", "long description"},
                {NamingConventions.DefaultNamingConventions.IDENTITY, "width", "width"},
                {NamingConventions.DefaultNamingConventions.IDENTITY, "longDescription", "longDescription"},
                {new BeanInfoNamingConvention(ExampleBean.class), "width", "width"},
                {new BeanInfoNamingConvention(ExampleBean.class), "longDescription", "Long Description Display Name"},
                {new PropertiesNamingConvention(NamingConventions.DefaultNamingConventions.UPPERCASE_WORDS, ExampleBean.class), "width", "Width"},
                {new PropertiesNamingConvention(NamingConventions.DefaultNamingConventions.UPPERCASE_WORDS, ExampleBean.class), "longDescription", "Long Description From Properties"},
        });
    }

    @Test
    public void toProperty() {
        assertEquals(property, namingConvention.toProperty(displayName));
    }

    @Test
    public void toDisplayName() {
        assertEquals(displayName, namingConvention.toDisplayName(property));
    }

    @Test
    public void roundtrip() {
        assertEquals(displayName, namingConvention.toDisplayName(namingConvention.toProperty(displayName)));
        assertEquals(property, namingConvention.toProperty(namingConvention.toDisplayName(property)));
    }
}
