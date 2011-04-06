package com.googlecode.mockarro.injector;

public interface MockEngine {
	public <T> T createMock(Class<T> clazz);
}
