package com.googlecode.mockarro.injector;

import java.lang.annotation.Annotation;

public interface InjectionEngine {
	public void inject(Object systemUnderTest, Class<? extends Annotation> injectionPoint, Object... mocks);
}
