package com.googlecode.mockarro.test;

import javax.inject.Inject;

public class SystemUnderTest {

    // this is meant to be used only by an injection engine
    @SuppressWarnings("unused")
    @Inject
    private Object fieldInjectionPoint;

    // It is created for testing purposes
    @SuppressWarnings("unused")
    private Object notMeantForInjection;


    // this is meant to be used only by an injection engine
    @SuppressWarnings("unused")
    @Inject
    private void setPrivatelyFieldInjectionPoint(final Object mock) {
        this.fieldInjectionPoint = mock;
    }


    @Inject
    public void setFieldInjectionPoint(final Object mock) {
        this.fieldInjectionPoint = mock;
    }


    public void regularMethod(final Object mock) {
        throw new IllegalStateException("Not meant to be executed.");
    }


    public SystemUnderTest() {
        // Intentionally empty
    }


    @Inject
    public SystemUnderTest(final Object mock) {
        fieldInjectionPoint = mock;
    }
}
