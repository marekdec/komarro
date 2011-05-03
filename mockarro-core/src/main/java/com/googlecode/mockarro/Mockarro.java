package com.googlecode.mockarro;

import static com.googlecode.mockarro.MethodSieve.methodsOf;
import static com.googlecode.mockarro.injector.Injector.withMockEngine;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.googlecode.mockarro.injector.InjectionPoint;
import com.googlecode.mockarro.injector.MockitoMockEngine;
import com.googlecode.mockarro.injector.InjectionEngine.Injection;

/**
 * A utility class that provides basic Mockarro functionality.
 * 
 * @author marekdec
 */
public class Mockarro<T> {

    private static Map<Thread, Set<Injection>> mocksByThread = Collections
                                                                     .synchronizedMap(new WeakHashMap<Thread, Set<Injection>>());

    private final Set<Injection>               mocks;

    private final Class<?>                     returnType;
    private final MockitoMockRecorder          recorder      = new MockitoMockRecorder();

    private final List<Class<?>>               genericTypes  = new ArrayList<Class<?>>();


    /**
     * Initialises Mockarro for given system under test. Its basic task is to
     * reset the test by creating and injecting mocks into the system under
     * test.
     * <p>
     * This method has to be invoked before every single test execution -
     * preferably within a before method (a method annotated with @Before in
     * JUnit 4.x or @BeforeMethod in TestNG).
     * 
     * @param systemUnderTest
     *            the unit that is going to be tested.
     */
    public static void initSut(final Object systemUnderTest) {
        final Set<Injection> injections = withMockEngine(new MockitoMockEngine()).createInjector().andInject(
                systemUnderTest);
        mocksByThread.put(Thread.currentThread(), injections);
    }


    /**
     * Sets one or more generic parameters of the return type.
     * <p>
     * TODO: provide api for nested generics
     * 
     * @param genericType
     *            first generic types
     * @param moreGenericTypes
     *            following generic types
     * @return self
     */
    public Mockarro<T> of(final Class<?> genericType, final Class<?>... moreGenericTypes) {
        this.genericTypes.add(genericType);
        this.genericTypes.addAll(Arrays.asList(moreGenericTypes));
        return this;
    }


    /**
     * Initialises Mockarro for given system under test and specifies the
     * injection point. Its basic task is to reset the test by creating and
     * injecting mocks into the system under test.
     * <p>
     * This method has to be invoked before every single test execution -
     * preferably within a before method (a method annotated with @Before in
     * JUnit 4.x or @BeforeMethod in TestNG).
     * 
     * @param systemUnderTest
     *            the unit that is going to be tested.
     * @param injectionPoint
     *            the injection point.
     */
    public static void initSut(final Object systemUnderTest, final InjectionPoint injectionPoint) {
        final Set<Injection> injections = withMockEngine(new MockitoMockEngine()).withInjectionPointAt(injectionPoint)
                .createInjector().andInject(systemUnderTest);
        mocksByThread.put(Thread.currentThread(), injections);
    }



    /**
     * Registers behaviour for all of the methods that return an object or a
     * primitive of a declared type.
     * <p>
     * Throws an {@link IllegalStateException} if the {@link #initSut(Object)}
     * method has not been invoked for the current system under test before
     * invoking this method.
     * 
     * @param <T>
     *            will be automatically inferred.
     * @param typeOfRequestedValue
     *            The type literal i.e. class or primitive literal. For example
     *            if the requested type is of MyClass type the
     *            typeOfRequestedValue will be MyClass.class, if the requested
     *            object is of a primitive integer type use int.class, etc.
     * @return a Mockarro object that can be used for ongoing stubbing.
     */
    public static <T> Mockarro<T> whenRequested(final Class<T> typeOfRequestedValue) {
        final Set<Injection> mockDescription = mocksByThread.get(Thread.currentThread());
        if (mockDescription == null) {
            throw new IllegalStateException(
                    "The mockarro test has not been initialized yet.\nAre you sure you have called the init method with the current unit under test as the parameter?");
        }
        return new Mockarro<T>(mockDescription, typeOfRequestedValue);
    }




    private Mockarro(final Set<Injection> mocks, final Class<?> returnType) {
        super();
        this.mocks = mocks;
        this.returnType = returnType;
    }


    /**
     * Defines the value that will be returned when object of defined type is
     * requested.
     * 
     * @param recordedValue
     *            the vale that will be returned by the mocked object when the
     *            mock object is requested to return the value of previously
     *            defined type.
     */
    public void thenReturn(final T recordedValue) {
        final Class<?>[] zeroOrMoreGenericTypes = genericTypes.toArray(new Class<?>[genericTypes.size()]);

        for (final Injection mockDescription : mocks) {

            for (final Method method : methodsOf(mockDescription.actualClass()).of(zeroOrMoreGenericTypes).thatReturn(
                    returnType).asSet()) {
                if (!method.getName().equals("hashCode") && !method.getName().equals("equals")) {
                    recorder.record(mockDescription.getMock(), method, recordedValue);
                }
            }
        }
    }
}
