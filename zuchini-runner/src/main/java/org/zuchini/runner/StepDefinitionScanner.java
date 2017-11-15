package org.zuchini.runner;

import org.zuchini.annotations.After;
import org.zuchini.annotations.Before;
import org.zuchini.annotations.StepAnnotation;
import org.zuchini.runner.internal.ClasspathScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

class StepDefinitionScanner extends ClasspathScanner {

    private static final Class<? extends Annotation> COMPAT_STEP_DEF_ANNOTATION = getClassForName("cucumber.runtime.java.StepDefAnnotation");
    private static final Class<? extends Annotation> COMPAT_BEFORE = getClassForName("cucumber.api.java.Before");
    private static final Class<? extends Annotation> COMPAT_AFTER = getClassForName("cucumber.api.java.After");

    private final List<StepDefinition> stepDefinitions;
    private final List<HookDefinition> hookDefinitions;

    StepDefinitionScanner(ClassLoader classLoader, List<String> packageNames) {
        super(classLoader, packageNames, ".class");
        this.stepDefinitions = new ArrayList<>();
        this.hookDefinitions = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Annotation> getClassForName(final String className) {
        try {
            final ClassLoader cl = StepDefinitionScanner.class.getClassLoader();
            return (Class<? extends Annotation>) Class.forName(className, false, cl);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static <T> T getAnnotationValue(Annotation annotation, Class<T> returnType) {
        try {
            final Method valueMethod = annotation.annotationType().getMethod("value");
            return returnType.cast(valueMethod.invoke(annotation));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Unexpected exception while getting annotation value");
        }
    }

    private static String getAnnotationValueString(final Annotation annotation) {
        return getAnnotationValue(annotation, String.class);
    }

    private static String[] getAnnotationValueStringArray(final Annotation annotation) {
        return getAnnotationValue(annotation, String[].class);
    }

    private static boolean isStepAnnotation(Annotation annotation) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        return annotationType.getAnnotation(StepAnnotation.class) != null;
    }

    private static boolean isCompatStepAnnotation(Annotation annotation) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        return COMPAT_STEP_DEF_ANNOTATION != null && annotationType.getAnnotation(COMPAT_STEP_DEF_ANNOTATION) != null;
    }

    private static boolean isBeforeHook(Annotation annotation) {
        return annotation.annotationType() == Before.class || annotation.annotationType() == COMPAT_BEFORE;
    }

    private static boolean isAfterHook(Annotation annotation) {
        return annotation.annotationType() == After.class || annotation.annotationType() == COMPAT_AFTER;
    }

    private static boolean isPublic(Method method) {
        return (method.getModifiers() & Modifier.PUBLIC) != 0;
    }

    private static Set<String> includedTags(String[] tags) {
        final Set<String> result = new HashSet<>(tags.length);
        for (String tag : tags) {
            if (!tag.startsWith("~")) {
                result.add(tag.startsWith("@") ? tag.substring(1) : tag);
            }
        }
        return result;
    }

    private static Set<String> excludedTags(String[] tags) {
        final Set<String> result = new HashSet<>(tags.length);
        for (String tag : tags) {
            if (tag.startsWith("~")) {
                result.add(tag.startsWith("~@") ? tag.substring(2) : tag.substring(1));
            }
        }
        return result;
    }

    @Override
    protected void handleResource(String classResource) {
        final String className = classResource.replace('/', '.').replaceFirst(".class$", "");
        try {
            final Class<?> clazz = Class.forName(className, false, classLoader);
            for (Method method : clazz.getDeclaredMethods()) {
                if (isPublic(method)) {
                    for (Annotation annotation : method.getAnnotations()) {
                        if (isStepAnnotation(annotation) || isCompatStepAnnotation(annotation)) {
                            final String value = getAnnotationValueString(annotation);
                            stepDefinitions.add(new StepDefinition(value, method));
                        } else if (isBeforeHook(annotation)) {
                            final String[] value = getAnnotationValueStringArray(annotation);
                            final Set<String> tags = new HashSet<>(asList(value));
                            hookDefinitions.add(new HookDefinition(HookDefinition.Event.BEFORE, tags, method));
                        } else if (isAfterHook(annotation)) {
                            final String[] value = getAnnotationValueStringArray(annotation);
                            final Set<String> tags = new HashSet<>(asList(value));
                            hookDefinitions.add(new HookDefinition(HookDefinition.Event.AFTER, tags, method));
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            System.err.println(classResource);
            e.printStackTrace();
        }
    }

    List<StepDefinition> getStepDefinitions() {
        return stepDefinitions;
    }

    List<HookDefinition> getHookDefinitions() {
        return hookDefinitions;
    }

}
