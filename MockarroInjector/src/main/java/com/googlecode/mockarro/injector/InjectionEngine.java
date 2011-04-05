package com.googlecode.mockarro.injector;

import java.util.Set;


public interface InjectionEngine {

    /**
     * Injects mocks into injection points of a given object.
     * 
     * @param systemUnderTest
     *            the object that is to be injected with the mocks
     * @param injectionPoint
     *            defines the injection point
     * @param mocks
     *            a list of mocked objected that will be used to populate
     *            injected fields, if no matching mock is found on this list it
     *            will be created using the mockEngine.
     * @return a set of mocks injected
     */
    public Set<Object> inject(Object systemUnderTest, InjectionPoint injectionPoint, Object... mocks);
}
