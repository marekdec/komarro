package com.googlecode.komarro;

import static com.googlecode.komarro.Injector.withMockEngine;
import static com.googlecode.komarro.MethodSieve.methodsOf;
import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.mockito.Mock;
import org.mockito.internal.util.MockUtil;

/**
 * Komarro provides a way to define test's indirect input without a necessity to
 * know the details of the the actual implementation of the unit under test. <br>
 * In other words, Komarro makes it possible to test a method of an object in
 * isolation from the collaborators, while at the same time the test method does
 * not have to know any details of the collaborators implementation (except for
 * the return types). Instead it gives you a way to define the indirect input of
 * the tested method.
 * <p>
 * The {@link #instanceForTesting(Class, MockDescriptor...)} or the
 * {@link #instanceForTesting(Class, InjectionPoint, MockDescriptor...)} method
 * should be invoked in order to create an instance of the unit under test. This
 * is the only way to initialize a Komarro test. During the instantiation
 * Komarro takes care of the injection and creation (if necessary) of the
 * collaborators. Constructor injection, field injection and setter injection
 * are supported (note that setter injection supports only single parameter
 * methods whose name starts with the <i>set</i> prefix. The Guice style "any
 * method injection" is not supported in this version. The
 * {@link #instanceForTesting(Class, MockDescriptor...)} methods are capable of
 * constructor injection given that at most one constructor is defined as
 * eligible for injection. Otherwise an error will be reported. The mocked
 * collaborators can be either passed to the instanceForTesting method in form
 * of {@link MockDescriptor}s or they can be omitted. Every collaborator that
 * does not have a mock object specified and passed to the instanceForTesting
 * method (and is required by the sut) will be created using the default the
 * {@link MockEngine}.
 * <p>
 * It is also possible to discover Mockito annotated mocks and to use them to
 * initialize Komarro. Use following idiom in order to do so:<br>
 * <code>
 * {@code instanceForTesting(typeOfSystemUnderTest, annotatedMocks(this))};
 * </code><br>
 * Note that the {@link MockitoMockDescriptionCreator#annotatedMocks(Object)}
 * method is intended to be statically imported from
 * {@link MockitoMockDescriptionCreator} utility class. In order to use the
 * idiom described above, the Mockito mocks have to be declared within the
 * current test class and they have to be initialized using standard Mockito
 * initialization procedure.
 * <p>
 * It is possible to mix the Mockito mocks behavior recording and verification
 * with the Komarro style mock behavior definition. The last thing declared will
 * override all previous declarations.
 * <p>
 * <p>
 * In order to define the indirect input to a tested method of type SomeClass to
 * be equal to someObject use a static import statement to import the
 * <b>Komarro.<i>given</i></b> method and use following syntax:
 * <p>
 * <code>
 * {@code given(SomeClass.class).isRequested().thenReturn(someObject);}
 * </code>
 * <p>
 * A synonym was defined for above declaration. It should be used when there is
 * a name clash with other statically imported methods named <i>given</i>. The
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
public final class Komarro {

	private static Map<Thread, Set<MockDescriptor>> mocksByThread = Collections
			.synchronizedMap(new WeakHashMap<Thread, Set<MockDescriptor>>());

	/**
	 * Creates an instance of a unit under test, initializes {@link Komarro}.
	 * This method has to be used to instantiate the unit under test. This finds
	 * the injection point among the most popular injection points (e.g. JEE6
	 * &#064;Inject, Seam 2 &#064;In or Spring &#064;Autowired annotations). It
	 * is also responsible for the injection of external (mocked) collaborators.
	 * It is possible to pass the complete set of mocked collaborators to this
	 * method or just a subset (or an empty set). In the latter case all
	 * necessary collaborators will be mocked using the default mockito mock
	 * engine.
	 * <p>
	 * This method has to be invoked before every single test execution -
	 * preferably within a before method (a method annotated with &#064;Before
	 * in JUnit 4.x or &#064;BeforeMethod in TestNG).
	 * 
	 * @param systemUnderTest
	 *            the unit that is going to be tested.
	 * @param injectionPoint
	 *            the injection point.
	 * @param mocks
	 *            descriptors of the user managed mocks that are to be injected
	 */
	public static <T> T instanceForTesting(
			final Class<T> typeOfSystemUnderTest, final MockDescriptor... mocks) {
		return createAndInjectMocks(typeOfSystemUnderTest, mocks);
	}

	/**
	 * Creates an instance of a unit under test and initializes {@link Komarro}.
	 * This method should to be used to instantiate the unit under test if the
	 * user wishes to pass a set of automatically discovered mocks and mocks
	 * specified manually.
	 * <p>
	 * It does have the same affect as
	 * {@link #instanceForTesting(Class, MockDescriptor...)}, the only
	 * difference is that it is possible to use following construct to
	 * initialize the system under test:
	 * <p>
	 * <code>instanceForTesting(annotatedMocks(this), mockedObject("A string to inject").withName("stringParam").ofType(String.class)</code>
	 * <p>
	 * This way it is possible to pass both the mocks discovered using the
	 * {@link MockitoMockDescriptionCreator#annotatedMocks(Object)} method and
	 * mocks created with the builder for {@link MockDescriptor}s. These mocks
	 * objects will be injected to the sut if a matching injection point is
	 * found. However, if no matching mock object is found for an injection
	 * point, a mock will be created and injected.
	 * <p>
	 * Note that this method should be used <b>only</b> if both mocks discovered
	 * among fields annotated with the {@literal @}{@link Mock} annotation and
	 * MockDescriptors created manually are to be injected into the system under
	 * test.
	 * 
	 * @see {@link #instanceForTesting(Class, MockDescriptor...)} for details
	 *      and other instructions.
	 * 
	 * @param systemUnderTest
	 *            the unit that is going to be tested.
	 * @param arrayOfMocks
	 *            an array of mocks, the origianl intention is to be able to
	 *            pass an array of mocks created by the
	 *            {@link MockitoMockDescriptionCreator#annotatedMocks(Object)}
	 *            method.
	 * @param injectionPoint
	 *            the injection point.
	 * @param mocks
	 *            descriptors of the user managed mocks that are to be injected
	 */
	public static <T> T instanceForTesting(
			final Class<T> typeOfSystemUnderTest,
			final MockDescriptor[] arrayOfMocks, final MockDescriptor... mocks) {
		List<MockDescriptor> allGivenMocks = new ArrayList<MockDescriptor>(
				asList(arrayOfMocks));
		if (mocks != null) {
			allGivenMocks.addAll(asList(mocks));
		}

		return createAndInjectMocks(typeOfSystemUnderTest,
				allGivenMocks.toArray(new MockDescriptor[allGivenMocks.size()]));
	}

	private static <T> T createAndInjectMocks(
			final Class<T> typeOfSystemUnderTest, MockDescriptor... mocks) {
		final SutDescriptor<T> sutDescriptor = withMockEngine(new MockEngine())
				.createInjector().instantiateAndInject(typeOfSystemUnderTest,
						mocks);

		mocksByThread.put(currentThread(), sutDescriptor.getMocks());
		return sutDescriptor.getSystemUnderTest();
	}

	/**
	 * Registers behavior for all of the methods that return an object of the
	 * specified type.
	 * <p>
	 * Throws an {@link IllegalStateException} if the object under test has not
	 * been created using the
	 * {@link #instanceForTesting(Class, MockDescriptor...)} method.
	 * 
	 * @param <T>
	 *            will be automatically inferred.
	 * @param typeOfRequestedValue
	 *            The type literal i.e. class or primitive literal. For example
	 *            if the requested type is of MyClass type the
	 *            typeOfRequestedValue will be MyClass.class, if the requested
	 *            object is of a primitive integer type use int.class, etc.
	 * @return a {@link Komarro} object that can be used for ongoing stubbing.
	 */
	public static <T> Assumption<T> given(
			final TypeLiteral<T> typeOfRequestedValue) {
		final Set<MockDescriptor> mockDescription = mocksByThread.get(Thread
				.currentThread());
		if (mockDescription == null) {
			throw new IllegalStateException(
					"The Komarro test has not been initialized yet.\nAre you sure the system under has been created using the instanceForTesting method?");
		}
		return new Assumption<T>(mockDescription, typeOfRequestedValue);
	}

	/**
	 * A version of {@link #given(TypeLiteral)} that should be used for
	 * non-generic return types.
	 * 
	 * @param typeOfRequestedValue
	 *            non-generic class literal
	 * @return ongoing {@link Komarro} stubbing
	 */
	public static <T> Assumption<T> given(final Class<T> typeOfRequestedValue) {
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
	 * @return a new {@link Assumption}
	 */
	public static <T> Assumption<T> givenObjectOf(
			final TypeLiteral<T> typeOfRequestedValue) {
		return given(typeOfRequestedValue);
	}

	/**
	 * A version of {@link #givenObjectOf(TypeLiteral)} that should be used for
	 * non-generic return types.
	 * 
	 * @param typeOfRequestedValue
	 * @return a new {@link Assumption}
	 */
	public static <T> Assumption<T> givenObjectOf(
			final Class<T> typeOfRequestedValue) {
		return given(typeOfRequestedValue);
	}

	public static class Assumption<T> {

		private final List<Class<?>> genericTypes = new ArrayList<Class<?>>();

		private final Set<MockDescriptor> mocks;
		private final TypeLiteral<?> returnTypeLiteral;

		public Assumption(Set<MockDescriptor> mocks,
				TypeLiteral<?> returnTypeLiteral) {
			super();
			this.mocks = mocks;
			this.returnTypeLiteral = returnTypeLiteral;
		}

		/**
		 * Prepares the stubbing to be recorded.
		 * 
		 * @return a new Stubbing
		 */
		public Stubbing<T> isRequested() {
			return new Stubbing<T>(mocks, genericTypes, returnTypeLiteral);
		}
	}

	public static class Stubbing<T> {

		private final MockUtil mockitoInternalUtils = new MockUtil();

		private final Set<MockDescriptor> mocks;

		private final TypeLiteral<?> returnTypeLiteral;
		private final MockitoMockRecorder recorder = new MockitoMockRecorder();

		private Stubbing(final Set<MockDescriptor> mocks,
				final List<Class<?>> genericTypes,
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
				if (canBeStubbed(descriptor)) {
					for (final Method method : methodsOf(
							(Class<?>) descriptor.getType()).thatReturn(
							returnTypeLiteral).asSet()) {
						if (!method.getName().equals("hashCode")
								&& !method.getName().equals("equals")) {
							recorder.record(descriptor.getMock(), method,
									recordedValue);
						}
					}
				}
			}
		}

		private boolean canBeStubbed(MockDescriptor descriptor) {
			return isMockitoMock(descriptor.getMock());
		}

		private boolean isMockitoMock(Object mock) {
			// Note that this routine is based on Mockito internals, if Mockito
			// changes this utility method in the future, Komarro should change
			// as well.
			return mockitoInternalUtils.isMock(mock);
		}
	}
}
