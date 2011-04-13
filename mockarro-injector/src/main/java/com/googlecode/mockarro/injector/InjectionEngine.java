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
    public Set<Injection> inject(Object systemUnderTest, InjectionPoint injectionPoint, Object... mocks);


    public static class Injection {
        private final Object   mock;
        private final Class<?> realClass;


        public Object getMock() {
            return mock;
        }


        /**
         * Returns the actual type of the mock. Mock engines tend to enhance the
         * classes of objects they mock and this may result in lose of some
         * crucial data e.g. Mockito erases the method signature for all methods
         * of an enhanced class.
         * 
         * @return
         */
        public Class<?> actualClass() {
            return realClass;
        }


        public Injection(final Object mock, final Class<?> realClass) {
            super();
            this.mock = mock;
            this.realClass = realClass;
        }
    }
}
