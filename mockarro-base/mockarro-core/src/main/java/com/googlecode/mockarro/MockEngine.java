package com.googlecode.mockarro;

import static org.mockito.Mockito.mock;

class MockEngine {

	public <T> T createMock(final Class<T> classToMock) {
		return mock(classToMock);
	}

}
