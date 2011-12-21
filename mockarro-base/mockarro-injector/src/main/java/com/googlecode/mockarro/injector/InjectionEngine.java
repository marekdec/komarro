package com.googlecode.mockarro.injector;

public interface InjectionEngine {

	/**
	 * Instantiates and injects mocks.
	 * 
	 * @param typeOfSystemUnderTest
	 *            type of the system under test that is to be created
	 * @param injectionPoint
	 *            defines the injection point
	 * @param mocks
	 *            a list of mocked objected that will be used to populate
	 *            injected fields, if no matching mock is found on this list it
	 *            will be created using the mockEngine.
	 * @return a set of mocks injected
	 */
	public <T> SutDescriptor<T> createAndInject(Class<T> typeOfSystemUnderTest,
			InjectionPoint injectionPoint, MockDescriptor... mocks);
}
