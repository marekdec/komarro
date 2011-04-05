package com.googlecode.mockarro.injector;

import static java.util.Arrays.asList;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * An {@link InjectionEngine} that uses reflection to inject smocks.
 * 
 * @author marekdec
 */
public class ReflectionInjectionEngine implements InjectionEngine {


    private final MockEngine mockEngine;


    public ReflectionInjectionEngine(final MockEngine mockEngine) {
        super();
        this.mockEngine = mockEngine;
    }


    /**
     * Creates and injects mocks into the injection points of given system under
     * test. Support fields injection and setter injection (for both private and
     * public fields and setters).
     * <p>
     * Throws {@link NullPointerException} is given system under test or
     * injection point is null.
     */
    public Set<Object> inject(final Object systemUnderTest, final InjectionPoint injectionPoint, final Object... mocks) {

        final Set<Object> createdMocks = new HashSet<Object>();

        injectElements(asList(systemUnderTest.getClass().getDeclaredFields()), systemUnderTest, injectionPoint,
                new InjectElementFunction<Field>() {

                    public void execute(final Field field) {
                        try {
                            final Object mock = mockEngine.createMock(field.getType());
                            createdMocks.add(mock);
                            field.set(systemUnderTest, mock);
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
                            final Object mock = mockEngine.createMock(type);
                            createdMocks.add(mock);
                            params.add(mock);
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

        return createdMocks;
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
