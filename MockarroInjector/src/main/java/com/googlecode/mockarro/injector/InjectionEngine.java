package com.googlecode.mockarro.injector;


public interface InjectionEngine {
    public void inject(Object systemUnderTest, InjectionPoint injectionPoint, Object... mocks);
}
