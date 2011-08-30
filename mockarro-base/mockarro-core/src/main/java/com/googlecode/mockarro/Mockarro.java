package com.googlecode.mockarro;

import static com.googlecode.mockarro.MethodSieve.methodsOf;
import static com.googlecode.mockarro.injector.Injector.withMockEngine;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.googlecode.mockarro.injector.InjectionPoint;
import com.googlecode.mockarro.injector.MockDescriptor;
import com.googlecode.mockarro.injector.MockitoMockEngine;

/**
 * Mockarro provides a way to define test's indirect input without a necessity to 
 * know the details of the the actual implementation of the unit under test. <br>
 * In other words, Mockarro makes it possible to test a method of a class that uses 
 * collaborators without any need to explicitly create collaborators' mock objects.
 * Instead it gives you a way to define the indirect input of the tested method.  
 * <p>
 * The {@link #initSut(Object, MockDescriptor...)} method should be invoked
 * before every test method in order to initialize and reset the Mockarro
 * engine. The initSut method injects the object under test with mocked
 * dependencies. The mock dependencies can be either passed to the initSut
 * method in form of {@link MockDescriptor}s or they can be omitted. Every mock
 * that is omitted (and is required by the sut) will be created using the
 * default the {@link MockitoMockEngine}.
 * <p>
 * It is also possible to discover Mockito annotated mocks and to use them to
 * initialize Mockarro. Use following idiom in order to do so:<br>
 * <code>
 * {@code initSut(systemUnderTest, annotatedMocks(this))};
 * </code><br>
 * Note that the {@link MockitoMockDescriptionCreator#annotatedMocks(Object)}
 * method is intended to be statically imported from
 * {@link MockitoMockDescriptionCreator} utility class.
 * <p>
 * <p>
 * In order to define the indirect input to a tested method of type SomeClass to
 * be equal to someObject use a static import statement to import the
 * <b>Mockarro.<i>given</i></b> method and use following syntax:
 * <p>
 * <code>
 * {@code given(SomeClass.class).isRequested().thenReturn(someObject);}
 * </code>
 * <p>
 * A synonym was defined for above declaration. It should be used when there is
 * a name class with other statically imported methods named <i>given</i>. The
 * outcome of both declarations is identical.
 * <p>
 * <code>
 * {@code givenObjectOf(SomeClass.class).isRequested().thenReturn(someObject);}
 * </code>
 * <p>
 * For generic return types {@link TypeLiteral} should be used:
 * <p>
 * <code>
 * {@code givenObjectOf(new TypeLiteral<GenericType<GenericParam>>() {}).isRequested().thenReturn(someObject);}
 * </code>
 * <p>
 * Note that in the above declaration double curly parenthesis was used in order
 * to retain generic class information at runtime by creating an anonymous
 * subtype of the generic {@link TypeLiteral}.
 * 
 * @author Marek Dec
 */
public final class Mockarro<T> {

    private static Map<Thread, Set<MockDescriptor>> mocksByThread = Collections
                                                                          .synchronizedMap(new WeakHashMap<Thread, Set<MockDescriptor>>());


    private final List<Class<?>>                    genericTypes  = new ArrayList<Class<?>>();
    private final Set<MockDescriptor>               mocks;

    private final TypeLiteral<?>                    returnTypeLiteral;


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
     * @param mocks
     *            descriptors of the user managed mocks that are to be injected
     */
    public static void initSut(final Object systemUnderTest, final MockDescriptor... mocks) {
        final Set<MockDescriptor> injections = withMockEngine(new MockitoMockEngine()).createInjector().andInject(
                systemUnderTest, mocks);
        mocksByThread.put(Thread.currentThread(), injections);
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
     * @param mocks
     *            descriptors of the user managed mocks that are to be injected
     */
    public static void initSut(final Object systemUnderTest, final InjectionPoint injectionPoint,
            final MockDescriptor... mocks) {
        final Set<MockDescriptor> injections = withMockEngine(new MockitoMockEngine())
                .withInjectionPointAt(injectionPoint).createInjector().andInject(systemUnderTest, mocks);
        mocksByThread.put(Thread.currentThread(), injections);
    }



    /**
     * Registers behaviour for all of the methods that return an object of the
     * specified type.
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
    public static <T> Mockarro<T> given(final TypeLiteral<T> typeOfRequestedValue) {
        final Set<MockDescriptor> mockDescription = mocksByThread.get(Thread.currentThread());
        if (mockDescription == null) {
            throw new IllegalStateException(
                    "The mockarro test has not been initialized yet.\nAre you sure you have called the init method with the current unit under test as the parameter?");
        }
        return new Mockarro<T>(mockDescription, typeOfRequestedValue);
    }


    /**
     * A version of {@link #given(TypeLiteral)} that should be used for
     * non-generic return types.
     * 
     * @param typeOfRequestedValue
     *            non-generic class literal
     * @return ongoing mockarro stubbing
     */
    public static <T> Mockarro<T> given(final Class<T> typeOfRequestedValue) {
        return given(TypeLiteral.create(typeOfRequestedValue));
    }


    /**
     * A synonym for {@link #given(Class)}. Should replace <i>given</i> every
     * time there is a name conflict with another <i>given</i> method imported
     * statically. The main intention was to avoid a name clash with
     * BDDMockito's <i>given</i> method.
     * 
     * @param <T>
     *            will be automatically inferred.
     * @param typeOfRequestedValue
     *            The type literal i.e. class or primitive literal. For example
     *            if the requested type is of MyClass type the
     *            typeOfRequestedValue will be MyClass.class, if the requested
     *            object is of a primitive integer type use int.class, etc.
     * @return
     */
    public static <T> Mockarro<T> givenObjectOf(final TypeLiteral<T> typeOfRequestedValue) {
        return given(typeOfRequestedValue);
    }


    /**
     * A version of {@link #givenObjectOf(TypeLiteral)} that should be used for
     * non-generic return types.
     * 
     * @param typeOfRequestedValue
     * @return
     */
    public static <T> Mockarro<T> givenObjectOf(final Class<T> typeOfRequestedValue) {
        return given(typeOfRequestedValue);
    }


    /**
     * Prepares the stubbing to be recorded.
     * 
     * @return a new Stubbing
     */
    public Stubbing<T> isRequested() {
        return new Stubbing<T>(mocks, genericTypes, returnTypeLiteral);
    }



    private Mockarro(final Set<MockDescriptor> mocks, final TypeLiteral<?> returnTypeLiteral) {
        super();
        this.mocks = mocks;
        this.returnTypeLiteral = returnTypeLiteral;
    }


    public static class Stubbing<T> {

        private final Set<MockDescriptor> mocks;

        private final TypeLiteral<?>      returnTypeLiteral;
        private final MockitoMockRecorder recorder = new MockitoMockRecorder();


        private Stubbing(final Set<MockDescriptor> mocks, final List<Class<?>> genericTypes,
                final TypeLiteral<?> returnTypeLiteral) {
            super();
            this.mocks = mocks;
            this.returnTypeLiteral = returnTypeLiteral;
        }


        /**
         * Defines the value that will be returned when object of defined type
         * is requested.
         * 
         * @param recordedValue
         *            the vale that will be returned by the mocked object when
         *            the mock object is requested to return the value of
         *            previously defined type.
         */
        public void thenReturn(final T recordedValue) {

            for (final MockDescriptor descriptor : mocks) {

                for (final Method method : methodsOf((Class<?>) descriptor.getType()).thatReturn(returnTypeLiteral)
                        .asSet()) {
                    if (!method.getName().equals("hashCode") && !method.getName().equals("equals")) {
                        recorder.record(descriptor.getMock(), method, recordedValue);
                    }
                }
            }
        }
    }
}
