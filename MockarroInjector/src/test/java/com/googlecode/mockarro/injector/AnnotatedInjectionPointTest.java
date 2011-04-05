package com.googlecode.mockarro.injector;

import static org.fest.assertions.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.googlecode.mockarro.test.SystemUnderTest;

public class AnnotatedInjectionPointTest {

    Class<SystemUnderTest> dummy = SystemUnderTest.class;


    @Test
    public void verifyElementsAreProperlyQualified() throws SecurityException, NoSuchFieldException,
            NoSuchMethodException {
        final AnnotatedInjectionPoint ip = new AnnotatedInjectionPoint(Inject.class);

        assertThat(ip.isInjectable(dummy.getDeclaredField("fieldInjectionPoint"))).isTrue();
        assertThat(ip.isInjectable(dummy.getDeclaredField("notMeantForInjection"))).isFalse();

        assertThat(ip.isInjectable(dummy.getDeclaredConstructor())).isFalse();
        assertThat(ip.isInjectable(dummy.getDeclaredConstructor(Object.class))).isTrue();

        assertThat(ip.isInjectable(dummy.getDeclaredMethod("setPrivatelyFieldInjectionPoint", Object.class))).isTrue();
        assertThat(ip.isInjectable(dummy.getDeclaredMethod("setFieldInjectionPoint", Object.class))).isTrue();
        assertThat(ip.isInjectable(dummy.getDeclaredMethod("regularMethod", Object.class))).isFalse();
    }


    @Test
    public void verifyConstructorAreProperlyFiltered() throws SecurityException, NoSuchMethodException {
        final AnnotatedInjectionPoint ip = new AnnotatedInjectionPoint(Inject.class, Constructor.class);
        assertThat(ip.isInjectable(dummy.getDeclaredConstructor(Object.class))).isFalse();
    }


    @Test
    public void verifyMethodsAreProperlyFiltered() throws SecurityException, NoSuchMethodException {
        final AnnotatedInjectionPoint ip = new AnnotatedInjectionPoint(Inject.class, Method.class);
        assertThat(ip.isInjectable(dummy.getDeclaredMethod("setPrivatelyFieldInjectionPoint", Object.class))).isFalse();
        assertThat(ip.isInjectable(dummy.getDeclaredMethod("setFieldInjectionPoint", Object.class))).isFalse();
    }


    @Test
    public void verifyFieldsAreProperlyFiltered() throws SecurityException, NoSuchFieldException {
        final AnnotatedInjectionPoint ip = new AnnotatedInjectionPoint(Inject.class, Field.class);
        assertThat(ip.isInjectable(dummy.getDeclaredField("fieldInjectionPoint"))).isFalse();
    }


    @Test
    public void verifyAllElementsAreProperlyFiltered() throws SecurityException, NoSuchFieldException,
            NoSuchMethodException {
        final AnnotatedInjectionPoint ip = new AnnotatedInjectionPoint(Inject.class, Field.class, Method.class,
                Constructor.class);
        assertThat(ip.isInjectable(dummy.getDeclaredField("fieldInjectionPoint"))).isFalse();
        assertThat(ip.isInjectable(dummy.getDeclaredMethod("setPrivatelyFieldInjectionPoint", Object.class))).isFalse();
        assertThat(ip.isInjectable(dummy.getDeclaredConstructor(Object.class))).isFalse();
    }

}
