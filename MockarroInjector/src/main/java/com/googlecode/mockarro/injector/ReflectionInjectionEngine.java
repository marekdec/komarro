package com.googlecode.mockarro.injector;

import java.lang.reflect.Field;

public class ReflectionInjectionEngine implements InjectionEngine {


    private final MockEngine mockEngine;


    public ReflectionInjectionEngine(final MockEngine mockEngine) {
        super();
        this.mockEngine = mockEngine;
    }


    public void inject(final Object systemUnderTest, final InjectionPoint injectionPoint, final Object... mocks) {

        for (final Field field : systemUnderTest.getClass().getDeclaredFields()) {
            if (injectionPoint.isInjectable(field)) {
                try {
                    final boolean wasAccesible = field.isAccessible();
                    field.setAccessible(true);
                    field.set(systemUnderTest, mockEngine.createMock(field.getType()));
                    field.setAccessible(wasAccesible);
                } catch (final IllegalAccessException e) {
                    throw new IllegalStateException("Setting field " + field.getName() + " in object of class "
                            + systemUnderTest.getClass() + " is unexpectedly forbidden.");
                }
            }
        }
    }

}
