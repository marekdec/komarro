package com.googlecode.mockarro.injector;

import java.util.Set;

import javax.inject.Inject;

public class Injector {

    private final InjectionEngine injectionEngine;
    private final InjectionPoint  injectionPoint;


    public static InjectorBuilder withMockEngine(final MockEngine mockEngine) {
        return new InjectorBuilder(mockEngine);
    }


    private Injector(final InjectionEngine injectionEngine, final InjectionPoint injectionPoint) {
        this.injectionEngine = injectionEngine;
        this.injectionPoint = injectionPoint;
    }


    public Set<Object> andInject(final Object systemUnderTest, final Object... mocks) {
        return inject(systemUnderTest, mocks);
    }


    public Set<Object> inject(final Object systemUnderTest, final Object... mocks) {
        return injectionEngine.inject(systemUnderTest, injectionPoint, mocks);
    }

    public static class InjectorBuilder {

        private InjectionEngine  injectionEngine;
        private final MockEngine mockEngine;
        private InjectionPoint   injectionPoint;


        public InjectorBuilder(final MockEngine engine) {
            this.mockEngine = engine;
        }


        public InjectorBuilder withInjectionPointAt(final InjectionPoint injectionPoint) {
            this.injectionPoint = injectionPoint;
            return this;
        }


        public InjectorBuilder using(final InjectionEngine engine) {
            this.injectionEngine = engine;
            return this;
        }


        public Injector createInjector() {
            if (injectionPoint == null) {
                injectionPoint = new AnnotatedInjectionPoint(Inject.class);
            }
            if (injectionEngine == null) {
                injectionEngine = new ReflectionInjectionEngine(mockEngine);
            }

            return new Injector(injectionEngine, injectionPoint);
        }
    }
}
