package org.zuchini.runner;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ConvertersTest {

    static class SomeBean {
        private final String value;

        SomeBean(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    static class SomeBeanConverter implements Converter<SomeBean> {
        @Override
        public SomeBean convert(String argument) {
            return new SomeBean(argument);
        }
    }


    private static class SingleBeanScope<T> implements Scope {

        private final Class<T> beanClass;
        private final T bean;

        public SingleBeanScope(Class<T> beanClass, T bean) {
            this.beanClass = beanClass;
            this.bean = bean;
        }

        @Override
        public void begin() {

        }

        @Override
        public <T> T getObject(Class<T> clazz) {
            return clazz == beanClass ? clazz.cast(bean) : null;
        }

        @Override
        public void end() {

        }
    }
    private static class EmptyScope implements Scope {

        @Override
        public void begin() {

        }

        @Override
        public <T> T getObject(Class<T> clazz) {
            return null;
        }
        @Override
        public void end() {

        }

    }

    static void methodWithConvertedParameter(@Convert(SomeBeanConverter.class) SomeBean someBean) {

    }

    static void methodWithNullableParameter(Integer value) {

    }

    static void methodWithPrimitiveParameter(int value) {

    }

    @Test
    public void shouldRetrieveConfiguredConverter() throws NoSuchMethodException {
        final SomeBeanConverter someBeanConverter = new SomeBeanConverter();
        final Scope scope = new SingleBeanScope<>(SomeBeanConverter.class, someBeanConverter);
        final Method method = ConvertersTest.class.getDeclaredMethod("methodWithConvertedParameter", SomeBean.class);
        final Converter<SomeBean> converter = Converters.getConverter(scope, SomeBean.class, method.getParameterAnnotations()[0]);

        Assert.assertNotNull("should not be null", converter);
        Assert.assertEquals("should be retrieved from scope", someBeanConverter, converter);
    }

    @Test
    public void shouldRetrieveDefaultConverter() {
        final Scope scope = new EmptyScope();

        final Converter<Integer> converter = Converters.getConverter(scope, Integer.class);

        Assert.assertNotNull("should not be null", converter);
        final int value = converter.convert("123");
        Assert.assertEquals("should convert integers", 123, value);
    }

    @Test(expected = NumberFormatException.class)
    public void shouldThrowOnPrimitiveNullParameter() {
        final Scope scope = new EmptyScope();
        final Converter<Integer> converter = Converters.getConverter(scope, Integer.TYPE);
        converter.convert(null);
    }

    @Test
    public void shouldConvertNullableParameter() {
        final Scope scope = new EmptyScope();
        final Converter<Integer> converter = Converters.getConverter(scope, Integer.class);

        Integer value = converter.convert(null);

        Assert.assertNull("should support null value", value);
    }

}
