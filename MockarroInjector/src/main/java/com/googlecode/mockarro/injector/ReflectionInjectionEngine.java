package com.googlecode.mockarro.injector;

import static java.util.Arrays.asList;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class ReflectionInjectionEngine implements InjectionEngine {


    private final MockEngine mockEngine;


    public ReflectionInjectionEngine(final MockEngine mockEngine) {
        super();
        this.mockEngine = mockEngine;
    }


    public void inject(final Object systemUnderTest, final InjectionPoint injectionPoint, final Object... mocks) {

        injectElements(asList(systemUnderTest.getClass().getDeclaredFields()), systemUnderTest, injectionPoint,
                new InjectElementFunction<Field>() {

                    public void execute(final Field field) {
                        try {
                            field.set(systemUnderTest, mockEngine.createMock(field.getType()));
                        } catch (final IllegalAccessException e) {
                            throw new IllegalStateException("Setting field " + field.getName() + " in object of class "
                                    + systemUnderTest.getClass() + " is unexpectedly forbidden.", e);
                        }
                    }
                });

        injectElements(asList(systemUnderTest.getClass().getDeclaredMethods()), systemUnderTest, injectionPoint,
                new InjectElementFunction<Method>() {

                    public void execute(final Method method) {
                        final List<Object> params = new LinkedList<Object>();
                        for (final Class<?> type : method.getParameterTypes()) {
                            params.add(mockEngine.createMock(type.getClass()));
                        }
                        try {
                            method.invoke(systemUnderTest, params.toArray());
                        } catch (final InvocationTargetException e) {
                            throw new IllegalStateException("Could not inject args " + params + " using method "
                                    + method.getName());
                        } catch (final IllegalAccessException e) {
                            throw new IllegalStateException("Setting field " + method.getName()
                                    + " in object of class " + systemUnderTest.getClass()
                                    + " is unexpectedly forbidden.", e);
                        }
                    }
                });
    }


    private <T extends AccessibleObject> void injectElements(final List<T> elements, final Object systemUnderTest,
            final InjectionPoint injectionPoint, final InjectElementFunction<T> injectFunction) {
        for (final T element : elements) {
            if (injectionPoint.isInjectable(element)) {
                final boolean wasAccesible = element.isAccessible();
                element.setAccessible(true);
                injectFunction.execute(element);
                element.setAccessible(wasAccesible);
            }
        }
    }

    private static interface InjectElementFunction<T extends AccessibleObject> {

        public void execute(final T element);
    }

}
