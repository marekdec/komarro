package com.googlecode.mockarro.injector;

import static com.googlecode.mockarro.injector.MockDescriptor.mockedObject;
import static java.util.Arrays.asList;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
     * Throws {@link NullPointerException} when given system under test or the
     * injection point is null.
     */
    public Set<MockDescriptor> inject(final Object systemUnderTest, final InjectionPoint injectionPoint,
            final MockDescriptor... mocks) {

        final Set<MockDescriptor> createdMocks = new HashSet<MockDescriptor>();

        final MockRepository mockRepository = new MockRepository(asList(mocks), mockEngine);

        injectElements(asList(systemUnderTest.getClass().getDeclaredFields()), systemUnderTest, injectionPoint,
                new InjectElementFunction<Field>() {

                    public void execute(final Field field) {
                        try {
                            final MockDescriptor descriptor = mockRepository.assignMock(field.getType(), field
                                    .getName());
                            createdMocks.add(descriptor);
                            field.set(systemUnderTest, descriptor.getMock());
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
                            final MockDescriptor descriptor = mockRepository.assignMock(type, method.getName());
                            createdMocks.add(descriptor);
                            params.add(descriptor.getMock());
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

    private static class MockRepository {

        private final Map<String, MockDescriptor> mockByName = new HashMap<String, MockDescriptor>();
        private final Map<Type, MockDescriptor>   mockByType = new HashMap<Type, MockDescriptor>();

        private final MockEngine                  mockEngine;


        private MockRepository(final List<MockDescriptor> mocks, final MockEngine engine) {
            for (final MockDescriptor descriptor : mocks) {
                mockByName.put(descriptor.getName(), descriptor);
                mockByType.put(descriptor.getType(), descriptor);
            }
            this.mockEngine = engine;
        }


        public MockDescriptor assignMock(final Class<?> type, final String elementName) {
            if (mockByName.containsKey(elementName)) {
                final MockDescriptor descriptor = mockByName.get(elementName);
                if (type.equals(mockByName.get(elementName).getType())) {
                    return descriptor;
                }
            } else if (isSetter(elementName) && mockByName.containsKey(toFieldName(elementName))) {
                final MockDescriptor descriptor = mockByName.get(toFieldName(elementName));
                if (type.equals(descriptor.getType())) {
                    return descriptor;
                }
            } else if (mockByType.containsKey(type)) {
                return mockByType.get(type);
            }
            return mockedObject(mockEngine.createMock(type)).withName(elementName).ofType(type);
        }


        private boolean isSetter(final String methodName) {
            return methodName.startsWith("set");
        }


        private String toFieldName(final String setterName) {
            return setterName.substring(3, 4).toLowerCase() + setterName.substring(4);
        }
    }
}
