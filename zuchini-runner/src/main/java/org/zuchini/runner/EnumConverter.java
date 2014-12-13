package org.zuchini.runner;

import javax.annotation.Nullable;

class EnumConverter<E extends Enum<E>> implements Converter<E> {
    private final Class<E> enumClass;

    EnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public E convert(@Nullable String argument) {
        if (argument == null || argument.length() == 0) {
            return null;
        } else {
            return Enum.valueOf(enumClass, argument);
        }
    }
}
