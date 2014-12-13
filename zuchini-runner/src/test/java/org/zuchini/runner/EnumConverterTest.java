package org.zuchini.runner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class EnumConverterTest {
    public enum Status {
        OPEN, CLOSED
    }

    private final String statusString;
    private final Status statusEnum;

    public EnumConverterTest(String statusString, Status statusEnum) {
        this.statusString = statusString;
        this.statusEnum = statusEnum;
    }

    @Parameterized.Parameters(name = "{0} -> {1}")
    public static Collection<Object[]> parameters() {
        return asList(new Object[][]{
                {"OPEN", Status.OPEN},
                {"CLOSED", Status.CLOSED},
                {"", null},
                {null, null}});
    }

    @Test
    public void shouldConvertEnum() {
        Converter<Status> enumConverter = DefaultConverterConfiguration.newEnumConverter(Status.class);
        assertEquals(statusEnum, enumConverter.convert(statusString));
    }
}
