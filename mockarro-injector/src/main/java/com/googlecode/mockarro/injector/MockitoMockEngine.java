package com.googlecode.mockarro.injector;

import static org.mockito.Mockito.mock;

public class MockitoMockEngine implements MockEngine {

	public <T> T createMock(Class<T> classToMock) {
		return mock(classToMock);
	}

}
