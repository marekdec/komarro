package com.googlecode.mockarro.test;

import javax.inject.Inject;

public class SystemUnderTest {

    @Inject
    private Object fieldInjectionPoint;

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


    public Object getFieldInjectionPoint() {
        return fieldInjectionPoint;
    }


    public Object getNotMeantForInjection() {
        return notMeantForInjection;
    }

}
