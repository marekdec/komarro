package com.googlecode.mockarro.injector;

/**
 * An entity capable of creating mock objects.
 * 
 * @author marekdec
 */
public interface MockEngine {
	public <T> T createMock(Class<T> clazz);
}
