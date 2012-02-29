package com.googlecode.komarro;

import java.lang.reflect.Type;

import org.mockito.MockitoAnnotations;

/**
 * Mock object descriptor. It is a container for the actual mocked object and
 * the data that is necessary to determine the injection point (i.e. the real
 * type of the mock Object may be lost during the enhancements conducted by
 * Mockito).
 * <p>
 * There is a fluent interface provided so that the MockDescriptor can be
 * created easily. The statically imported {@link #mockedObject(Object)} method
 * should be used to start the {@link MockDescriptor} instantiation.
 * <p>
 * <b>Examples of usage:</b><br>
 * <code>mockedObject(Mockito.mock(Collaborator.class)).withName("firstCollaborator").ofType(Collaborator.class)</code>
 * <code>mockedObject("A configuration string").ofType(String.class)</code>
 * <p>
 * The {@link MockDescriptorBuilder#withName(String) withName} parameter is
 * optional and does not have to be provided. It is used by the injection engine
 * to inject the mock to a field or a setter whose name matches the specified.
 * If it is not given or no matching field/setter is found <i>by type</i>
 * injection will be intended. In order to match a setter, the set prefix should
 * be omitted (e.g. to inject a method called setFirstName - withName should be
 * given <code>"firstName"</code> value).
 * <p>
 * It is possible to pass object that are not mocks, they will be injected
 * according to the injection rules.
 * <p>
 * If the external Mockito mocks created with the <code>{@literal @}Mock</code>
 * and {@link MockitoAnnotations#initMocks(Object)} are to be passed to the
 * {@link Komarro#instanceForTesting(Class, MockDescriptor...)
 * instanceForTesting} method, the {@link MockitoMockDescriptionCreator} can be
 * used.
 * 
 * 
 * @author marekdec
 */
public final class MockDescriptor {
	private final Object mock;
	private final String name;
	private final Type type;

	/**
	 * Creates the immutable {@link MockDescriptor}.
	 * 
	 * @param mock
	 * @param name
	 * @param type
	 */
	private MockDescriptor(final Object mock, final String name, final Type type) {
		super();
		this.mock = mock;
		this.name = name;
		this.type = type;
	}

	/**
	 * Returns the actual mocked object.
	 * 
	 * @return
	 */
	public Object getMock() {
		return mock;
	}

	/**
	 * Returns the name of the mock - in most cases this reflects the name of
	 * the variable that contains the mock.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * The actual type of the mocked object. This should be use when determining
	 * the type of the mocked object as the mocking frameworks tend to manipuate
	 * the bytecode at runtime and as a result the mockObject.getClass() does
	 * not return the original type.
	 * 
	 * @return
	 */
	public Type getType() {
		return type;
	}

	public static MockDescriptorBuilder mockedObject(final Object mock) {
		return new MockDescriptorBuilder(mock);
	}

	@Override
	public String toString() {
		return "Mock: " + mock + " of type [" + type + "] "
				+ (name == null ? "with no name" : "named [" + name + "]");
	}

	public final static class MockDescriptorBuilder {
		private final Object mock;
		private String name;

		private MockDescriptorBuilder(final Object mock) {
			super();
			this.mock = mock;
		}

		public MockDescriptorBuilder withName(final String name) {
			this.name = name;
			return this;
		}

		public MockDescriptor ofType(final Type type) {
			return new MockDescriptor(mock, name, type);
		}

	}
}
