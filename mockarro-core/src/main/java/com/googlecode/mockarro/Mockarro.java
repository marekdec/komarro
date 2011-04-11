package com.googlecode.mockarro;

import static com.googlecode.mockarro.injector.Injector.withMockEngine;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.googlecode.mockarro.injector.MockitoMockEngine;

public class Mockarro {

    private static Map<Object, Set<Object>> mocksBySut = new WeakHashMap<Object, Set<Object>>();


    public static void initSut(final Object systemUnderTest) {
        final Set<Object> injectedMocks = withMockEngine(new MockitoMockEngine()).createInjector().andInject(
                systemUnderTest);
        mocksBySut.put(systemUnderTest, injectedMocks);
    }


    public static SutOperation whenever(final Object systemUnderTest) {
        return new SutOperation(mocksBySut.get(systemUnderTest));
    }


    private Mockarro() {
        // EMPTY ON PURPOSE
    }

    public static class SutOperation {

        private final Set<Object> mocks;


        public SutOperation(final Set<Object> mocks) {
            super();
            this.mocks = mocks;
        }


        public BehaviorDefinition requests(final Class<?> clazz) {
            return null;
        }
    }

    public static class BehaviorDefinition {
        public void thenReturn(final Class<?> clazz) {

        }
    }

}
