package com.googlecode.mockarro;

import static com.googlecode.mockarro.MethodSieve.methodsOf;
import static com.googlecode.mockarro.injector.Injector.withMockEngine;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.googlecode.mockarro.injector.InjectionEngine.Injection;
import com.googlecode.mockarro.injector.MockitoMockEngine;

public class Mockarro {

    private static Map<Object, Set<Injection>> mocksBySut = new WeakHashMap<Object, Set<Injection>>();

    private final Set<Injection>               mockDescription;


    public static void initSut(final Object systemUnderTest) {
        final Set<Injection> injections = withMockEngine(new MockitoMockEngine()).createInjector().andInject(
                systemUnderTest);
        mocksBySut.put(systemUnderTest, injections);
    }


    private Mockarro(final Set<Injection> mocks) {
        super();
        this.mockDescription = mocks;
    }


    public static Mockarro whenever(final Object systemUnderTest) {
        return new Mockarro(mocksBySut.get(systemUnderTest));
    }



    public <T> BehaviorRegistration<T> requests(final Class<T> typeOfRequestedValue) {
        return new BehaviorRegistration<T>(mockDescription, typeOfRequestedValue);
    }

    public static class BehaviorRegistration<T> {
        private final Set<Injection>      mocks;

        private final Class<?>            returnType;
        private final MockitoMockRecorder recorder = new MockitoMockRecorder();


        private BehaviorRegistration(final Set<Injection> mocks, final Class<?> returnType) {
            super();
            this.mocks = mocks;
            this.returnType = returnType;
        }


        public void thenReturn(final T recordedValue) {
            for (final Injection mockDescription : mocks) {

                for (final Method method : methodsOf(mockDescription.actualClass()).thatReturn(returnType).asSet()) {
                    if (!method.getName().equals("hashCode") && !method.getName().equals("equals")) {
                        recorder.record(mockDescription.getMock(), method, recordedValue);
                    }
                }
            }
        }
    }

}
