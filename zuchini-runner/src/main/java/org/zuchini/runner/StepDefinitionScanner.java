package org.zuchini.runner;

import org.zuchini.annotations.StepAnnotation;
import org.zuchini.runner.internal.ClasspathScanner;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

class StepDefinitionScanner extends ClasspathScanner {

    @SuppressWarnings("unchecked")
    private static Class<? extends Annotation> getCompatStepDefAnnotation() {
        try {
            return (Class<? extends Annotation>) Class.forName("cucumber.runtime.java.StepDefAnnotation");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static final Class<? extends Annotation> COMPAT_STEP_DEF_ANNOTATION = getCompatStepDefAnnotation();

    private final List<StepDefinition> stepDefinitions;

    private StepDefinitionScanner(ClassLoader classLoader, List<String> packageNames) {
        super(classLoader, packageNames, ".class");
        this.stepDefinitions = new ArrayList<>();
    }

    private static String getAnnotationValue(Annotation annotation) {
        try {
            Method valueMethod = annotation.getClass().getDeclaredMethod("value");
            return (String) valueMethod.invoke(annotation);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassCastException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean isStepAnnotation(Annotation annotation) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        return annotationType.getAnnotation(StepAnnotation.class) != null;
    }

    private static boolean isCompatStepAnnotation(Annotation annotation) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        return COMPAT_STEP_DEF_ANNOTATION != null && annotationType.getAnnotation(COMPAT_STEP_DEF_ANNOTATION) != null;
    }

    private static boolean isPublic(Method method) {
        return (method.getModifiers() & Modifier.PUBLIC) != 0;
    }

    @Override
    protected void handleResource(String classResource) {
        String className = classResource.replace('/', '.').replaceFirst(".class$", "");
        try {
            Class<?> clazz = Class.forName(className, false, classLoader);
            for (Method method : clazz.getDeclaredMethods()) {
                if (isPublic(method)) {
                    for (Annotation annotation : method.getAnnotations()) {
                        if (isStepAnnotation(annotation) || isCompatStepAnnotation(annotation)) {
                            String value = getAnnotationValue(annotation);
                            stepDefinitions.add(new StepDefinition(value, method));
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

    public static List<StepDefinition> scan(ClassLoader classLoader, List<String> packageNames) throws IOException {
        StepDefinitionScanner scanner = new StepDefinitionScanner(classLoader, packageNames);
        scanner.scan();
        return scanner.getStepDefinitions();
    }

    public static List<StepDefinition> scan(ClassLoader classLoader, String... packageNames) throws IOException {
        return scan(classLoader, asList(packageNames));
    }

}
