package com.googlecode.komarro;

import static com.googlecode.komarro.MockDescriptor.mockedObject;
import static java.util.Arrays.asList;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An injecting mechanism that uses reflection to inject mocks.
 * 
 * @author marekdec
 */
final class InjectionEngine {

	private final MockEngine mockEngine;

	public InjectionEngine(final MockEngine mockEngine) {
		super();
		this.mockEngine = mockEngine;
	}

	public <T> SutDescriptor<T> createAndInject(Class<T> typeOfSystemUnderTest,
			InjectionPoint injectionPoint, MockDescriptor... mocks) {

		Constructor<?> noArgumentsCtor = null;
		Constructor<?> injectionCapableCtor = null;
		for (Constructor<?> ctor : typeOfSystemUnderTest.getConstructors()) {
			Class<?>[] params = ctor.getParameterTypes();
			if (params.length == 0) {
				noArgumentsCtor = ctor;
			} else {
				if (injectionPoint.isInjectable(ctor)) {
					if (injectionCapableCtor == null) {
						injectionCapableCtor = ctor;
					} else {
						throw new IllegalArgumentException(
								"The system under test of type "
										+ typeOfSystemUnderTest
										+ " cannot be initialized by Komarro as it does have more than one constructors eligible for the constructor injection.");
					}
				}
			}
		}

		if (injectionCapableCtor != null) {
			return doConstructorInjectionAndInjectElements(
					typeOfSystemUnderTest, injectionPoint,
					injectionCapableCtor, mocks);
		} else if (noArgumentsCtor != null) {
			return instantiateWithNoArgsAndInject(typeOfSystemUnderTest,
					injectionPoint, noArgumentsCtor, mocks);
		} else {
			throw new IllegalArgumentException(
					"The system under test of type "
							+ typeOfSystemUnderTest
							+ " cannot be initialized by Komarro as it does not have either a no-argument constructor or a single constructor eligible for injection.");
		}

	}

	private <T> SutDescriptor<T> doConstructorInjectionAndInjectElements(
			Class<T> typeOfSystemUnderTest, InjectionPoint injectionPoint,
			Constructor<?> injectionCapableCtor, MockDescriptor... mocks) {
		final MockRepository mockRepository = new MockRepository(asList(mocks),
				mockEngine);
		final Set<MockDescriptor> createdMocks = new HashSet<MockDescriptor>();
		Class<?>[] params = injectionCapableCtor.getParameterTypes();
		Object[] mocksForCtorParameters = new Object[params.length];

		int i = 0;
		for (Class<?> paramType : params) {
			final MockDescriptor descriptor = mockRepository.assignMock(
					paramType, null, AccessibleObjectType.CONSTRUCTOR);
			createdMocks.add(descriptor);

			mocksForCtorParameters[i++] = descriptor.getMock();
		}

		try {
			// This cast is type safe as the constructor is a constructor of
			// an object of the T type
			@SuppressWarnings("unchecked")
			T systemUnderTest = (T) injectionCapableCtor
					.newInstance(mocksForCtorParameters);
			return new SutDescriptor<T>(inject(systemUnderTest, injectionPoint,
					createdMocks, mockRepository), systemUnderTest);
		} catch (InstantiationException e) {
			throw cannotCreateSutException(typeOfSystemUnderTest);
		} catch (IllegalAccessException e) {
			throw cannotCreateSutException(typeOfSystemUnderTest);
		} catch (InvocationTargetException e) {
			throw cannotCreateSutException(typeOfSystemUnderTest);
		}
	}

	private <T> SutDescriptor<T> instantiateWithNoArgsAndInject(
			Class<T> typeOfSystemUnderTest, InjectionPoint injectionPoint,
			Constructor<?> noArgumentsCtor, MockDescriptor... mocks) {
		try {
			// This cast is type safe as the constructor is a constructor of
			// an object of the T type
			@SuppressWarnings("unchecked")
			T systemUnderTest = (T) noArgumentsCtor.newInstance();

			return new SutDescriptor<T>(inject(systemUnderTest, injectionPoint,
					new HashSet<MockDescriptor>(), new MockRepository(
							asList(mocks), mockEngine)), systemUnderTest);

		} catch (InstantiationException e) {
			throw cannotCreateSutException(typeOfSystemUnderTest);
		} catch (IllegalAccessException e) {
			throw cannotCreateSutException(typeOfSystemUnderTest);
		} catch (InvocationTargetException e) {
			throw cannotCreateSutException(typeOfSystemUnderTest);
		}
	}

	private <T> IllegalStateException cannotCreateSutException(
			Class<T> typeOfSystemUnderTest) {
		return new IllegalStateException("Unexpectedly the object of type ["
				+ typeOfSystemUnderTest + "] could not be initialized.");
	}

	private List<Field> getDeclaredAndSuperclassHierarchyFields(Class<?> type) {
		List<Field> fields = new ArrayList<Field>(
				asList(type.getDeclaredFields()));

		if (type.getSuperclass() != null) {
			fields.addAll(getDeclaredAndSuperclassHierarchyFields(type
					.getSuperclass()));
		}

		return fields;
	}

	private List<Method> getDeclaredAndSuperclassHierarchyMethods(Class<?> type) {
		List<Method> methods = new ArrayList<Method>(
				asList(type.getDeclaredMethods()));

		if (type.getSuperclass() != null) {
			methods.addAll(getDeclaredAndSuperclassHierarchyMethods(type
					.getSuperclass()));
		}

		return methods;
	}

	/**
	 * Creates and injects mocks into the injection points of given system under
	 * test. Support fields injection and setter injection (for both private and
	 * public fields and setters).
	 * <p>
	 * Throws {@link NullPointerException} when given system under test or the
	 * injection point is null.
	 */
	private Set<MockDescriptor> inject(final Object systemUnderTest,
			final InjectionPoint injectionPoint,
			final Set<MockDescriptor> createdMocks,
			final MockRepository mockRepository) {

		injectElements(
				getDeclaredAndSuperclassHierarchyFields(systemUnderTest
						.getClass()),
				systemUnderTest, injectionPoint,
				new InjectElementFunction<Field>() {

					public void execute(final Field field) {
						try {
							final MockDescriptor descriptor = mockRepository
									.assignMock(field.getType(),
											field.getName(),
											AccessibleObjectType.FIELD);
							createdMocks.add(descriptor);
							field.set(systemUnderTest, descriptor.getMock());
						} catch (final IllegalAccessException e) {
							throw new IllegalStateException("Setting field "
									+ field.getName() + " in object of class "
									+ systemUnderTest.getClass()
									+ " is unexpectedly forbidden.", e);
						}
					}
				});

		injectElements(
				getDeclaredAndSuperclassHierarchyMethods(systemUnderTest
						.getClass()),
				systemUnderTest, injectionPoint,
				new InjectElementFunction<Method>() {

					public void execute(final Method method) {
						final List<Object> params = new LinkedList<Object>();
						for (final Class<?> type : method.getParameterTypes()) {
							final MockDescriptor descriptor = mockRepository
									.assignMock(type, method.getName(),
											AccessibleObjectType.METHOD);
							createdMocks.add(descriptor);
							params.add(descriptor.getMock());
						}
						try {
							method.invoke(systemUnderTest, params.toArray());
						} catch (final InvocationTargetException e) {
							throw new IllegalStateException(
									"Could not inject args " + params
											+ " using method "
											+ method.getName());
						} catch (final IllegalAccessException e) {
							throw new IllegalStateException("Setting field "
									+ method.getName() + " in object of class "
									+ systemUnderTest.getClass()
									+ " is unexpectedly forbidden.", e);
						}
					}
				});

		return createdMocks;
	}

	private <T extends AccessibleObject> void injectElements(
			final List<T> elements, final Object systemUnderTest,
			final InjectionPoint injectionPoint,
			final InjectElementFunction<T> injectFunction) {
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
		private final Map<Type, MockDescriptor> mockByType = new HashMap<Type, MockDescriptor>();

		private final MockEngine mockEngine;

		private MockRepository(final List<MockDescriptor> mocks,
				final MockEngine engine) {
			for (final MockDescriptor descriptor : mocks) {
				if (descriptor.getName() != null) {
					mockByName.put(descriptor.getName(), descriptor);
				}
				mockByType.put(descriptor.getType(), descriptor);
			}
			this.mockEngine = engine;
		}

		public MockDescriptor assignMock(final Class<?> type,
				final String elementName, final AccessibleObjectType elementType) {
			if (mockByName.containsKey(elementName)) {
				final MockDescriptor descriptor = mockByName.get(elementName);
				if (type.equals(mockByName.get(elementName).getType())) {
					return descriptor;
				}
			} else if (isSetter(elementName)
					&& mockByName.containsKey(toFieldName(elementName))) {
				final MockDescriptor descriptor = mockByName
						.get(toFieldName(elementName));
				if (type.equals(descriptor.getType())) {
					return descriptor;
				}
			} else if (mockByType.containsKey(type)) {
				return mockByType.get(type);
			}

			Object createdMock = tryCreatingMock(type, elementName, elementType);

			return mockedObject(createdMock).withName(elementName).ofType(type);
		}

		private Object tryCreatingMock(final Class<?> type, String elementName,
				AccessibleObjectType elementType) {
			try {
				return mockEngine.createMock(type);
			} catch (Exception e) {
				throw new IllegalArgumentException(
						"The mock of type ["
								+ type
								+ "] that was tried to be injected into ["
								+ (elementType == AccessibleObjectType.CONSTRUCTOR ? "Constructor"
										: elementName)
								+ "] cannot be created. It is probably caused by some limitations "
								+ "of the underlying Mockito mock engine. Please see the exception "
								+ "cause for details.", e);
			}
		}

		private boolean isSetter(final String methodName) {
			return methodName != null && methodName.startsWith("set");
		}

		private String toFieldName(final String setterName) {
			return setterName.substring(3, 4).toLowerCase()
					+ setterName.substring(4);
		}
	}

	private static enum AccessibleObjectType {
		CONSTRUCTOR, FIELD, METHOD
	}
}
