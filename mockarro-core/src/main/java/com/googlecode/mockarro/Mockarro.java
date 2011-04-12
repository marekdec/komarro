package com.googlecode.mockarro;

import static com.googlecode.mockarro.MethodSieve.methodsOf;
import static com.googlecode.mockarro.injector.Injector.withMockEngine;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.googlecode.mockarro.injector.MockitoMockEngine;

public class Mockarro {

    private static Map<Object, Set<Object>> mocksBySut = new WeakHashMap<Object, Set<Object>>();

    private final Set<Object>               mocks;


    public static void initSut(final Object systemUnderTest) {
        final Set<Object> injectedMocks = withMockEngine(new MockitoMockEngine()).createInjector().andInject(
                systemUnderTest);
        mocksBySut.put(systemUnderTest, injectedMocks);
    }


    private Mockarro(final Set<Object> mocks) {
        super();
        this.mocks = mocks;
    }


    public static Mockarro whenever(final Object systemUnderTest) {
        return new Mockarro(mocksBySut.get(systemUnderTest));
    }



    public <T> BehaviorRegistration<T> requests(final Class<T> typeOfRequestedValue) {
        return new BehaviorRegistration<T>(mocks, typeOfRequestedValue);
    }

    public static class BehaviorRegistration<T> {
        private final Set<Object>         mocks;

        private final Class<?>            returnType;
        private final MockitoMockRecorder recorder = new MockitoMockRecorder();


        private BehaviorRegistration(final Set<Object> mocks, final Class<?> returnType) {
            super();
            this.mocks = mocks;
            this.returnType = returnType;
        }


        public void thenReturn(final T recordedValue) {
            for (final Object mock : mocks) {

                for (final Method method : methodsOf(mock).thatReturn(returnType).asSet()) {
                    if (!method.getName().equals("hashCode") && !method.getName().equals("equals")) {
                        recorder.record(mock, method, recordedValue);
                    }
                }
            }
        }
    }

}
