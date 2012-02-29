package com.googlecode.komarro;

import static com.googlecode.komarro.MockDescriptor.mockedObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * The main objective of this utility class is to provide methods that
 * facilitate the MockDescriptors creation in the Mockito annotation based
 * tests.
 * 
 * @author marekdec
 */
public final class MockitoMockDescriptionCreator {

	private MockitoMockDescriptionCreator() {
		// empty on purpose, prevents the user from instantiating this utility
		// class
	}

	/**
	 * Returns an array of mock descriptors for the objects contained by the
	 * fields annotated with the Mockito {@link Mock} annotation.
	 * 
	 * @param testClass
	 *            the class that contains field annotated with the {@link Mock}
	 *            annotation. The mocks are expected to be already created (with
	 *            Mockito {@link MockitoAnnotations#initMocks(Object)} method).
	 * @return an array of MockDescriptors, every {@link MockDescriptor}
	 *         corresponds to a single object that is referenced by a single
	 *         field of the testClass annotated with the {@link Mock}
	 *         annotation.
	 */
	public static MockDescriptor[] annotatedMocks(final Object testClass) {
		final List<MockDescriptor> descriptors = new ArrayList<MockDescriptor>();

		for (final Field field : testClass.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(Mock.class)) {
				try {
					final boolean wasAccessible = field.isAccessible();
					field.setAccessible(true);
					descriptors.add(mockedObject(field.get(testClass))
							.withName(field.getName()).ofType(
									field.getGenericType()));
					field.setAccessible(wasAccessible);

				} catch (final IllegalAccessException e) {
					throw new IllegalStateException(
							"Critical error, cannot create mock descriptors as the field "
									+ field.getName()
									+ " annotated with @Mock annotation is not accesible.",
							e);
				}
			}
		}

		return descriptors.toArray(new MockDescriptor[descriptors.size()]);
	}
}
