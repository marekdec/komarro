package com.googlecode.mockarro.injector;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

public class Injector {

	private InjectionEngine injectionEngine;
	private Class<? extends Annotation> injectionPoint;

	public static InjectorBuilder withMockEngine(MockEngine mockEngine) {
		return new InjectorBuilder(mockEngine);
	}
	
	
	private Injector(InjectionEngine injectionEngine,
			Class<? extends Annotation> injectionPoint) {
		this.injectionEngine = injectionEngine;
		this.injectionPoint = injectionPoint;
	}

	public void andInject(Object systemUnderTest, Object... mocks) {
		inject(systemUnderTest, mocks);
	}

	public void inject(Object systemUnderTest, Object... mocks) {
		injectionEngine.inject(systemUnderTest, injectionPoint, mocks);
	}

	public static class InjectorBuilder {
		
		private InjectionEngine injectionEngine;
		private MockEngine mockEngine;
		private Class<? extends Annotation> injectionPoint;
		
		public InjectorBuilder(MockEngine engine) {
			this.mockEngine = engine;
		}
		
		public InjectorBuilder withInjectionPointAt(Class<? extends Annotation> injectionPoint) {
			this.injectionPoint = injectionPoint; 
			return this;
		}
		
		public InjectorBuilder using(InjectionEngine engine) {
			this.injectionEngine = engine;
			return this;
		}
		
		public Injector createInjector() {
			if (injectionPoint == null) {
				injectionPoint = Inject.class;
			} 
			if (injectionEngine == null) {
				injectionEngine = new ReflectionInjectionEngine(mockEngine);
			}
			
			return new Injector(injectionEngine, injectionPoint);
		}
	}
}
