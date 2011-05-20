package com.googlecode.mockarro.injector;

import static org.mockito.Mockito.mock;

public final class MockitoMockEngine implements MockEngine {

    public <T> T createMock(final Class<T> classToMock) {
        return mock(classToMock);
    }

}
