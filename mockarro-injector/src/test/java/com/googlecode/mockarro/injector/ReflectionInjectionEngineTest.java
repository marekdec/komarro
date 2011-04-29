package com.googlecode.mockarro.injector;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.inject.Inject;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.googlecode.mockarro.test.SystemUnderTest;

public class ReflectionInjectionEngineTest {

    @Mock
    private MockEngine                mockedMockEngine;

    private ReflectionInjectionEngine engine;


    @BeforeMethod
    public void init() {
        initMocks(this);
    }


    @Test
    public void injectNotAccessibleObjects() {
        // prepare
        when(mockedMockEngine.createMock(Object.class)).thenReturn(new Object()).thenReturn(new Object())
                .thenReturn(new Object());
        engine = new ReflectionInjectionEngine(mockedMockEngine);

        final SystemUnderTest sut = new SystemUnderTest();

        // invoke
        engine.inject(sut, new AnnotatedInjectionPoint(Inject.class));

        // verify
        assertThat(sut.getFieldInjectionPoint()).isNotNull().isInstanceOf(Object.class);
        assertThat(sut.getNotMeantForInjection()).isNull();

        assertThat(sut.getUsedBySetterInjector()).isNotNull();
        assertThat(sut.getUsedByPrivateSetterInjector()).isNotNull();
    }
}
