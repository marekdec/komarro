package com.googlecode.mockarro.injector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ReflectionInjectionEngine implements InjectionEngine {

	
	private final MockEngine mockEngine;
	
	public ReflectionInjectionEngine(MockEngine mockEngine) {
		super();
		this.mockEngine = mockEngine;
	}


	public void inject(Object systemUnderTest, Class<? extends Annotation> injectionPoint,
			Object... mocks) {
		
		for (Field field : systemUnderTest.getClass().getDeclaredFields()) {
			if (field.getAnnotation(injectionPoint) != null) {
				try {
					boolean wasAccesible = field.isAccessible();
					field.setAccessible(true);
					field.set(systemUnderTest, mockEngine.createMock(field.getType()));
					field.setAccessible(wasAccesible);
				} catch (IllegalAccessException e) {
					throw new IllegalStateException("Setting field " + field.getName() + " in object of class " + systemUnderTest.getClass() + " is unexpectedly forbidden.");
				}
			}
		} 
	}

}
