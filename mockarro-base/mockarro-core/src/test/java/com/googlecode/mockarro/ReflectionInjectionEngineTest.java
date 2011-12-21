package com.googlecode.mockarro;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.inject.Inject;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.googlecode.mockarro.testclasses.ClassWithFieldAndSetterInjector;

public class ReflectionInjectionEngineTest {

	@Mock
	private MockEngine mockedMockEngine;

	private InjectionEngine engine;

	@BeforeMethod
	public void init() {
		initMocks(this);
	}

	@Test
	public void injectNotAccessibleObjects() {
		// given
		when(mockedMockEngine.createMock(Object.class))
				.thenReturn(new Object()).thenReturn(new Object())
				.thenReturn(new Object());
		engine = new InjectionEngine(mockedMockEngine);

		// when
		SutDescriptor<ClassWithFieldAndSetterInjector> sutDescriptor = engine.createAndInject(
				ClassWithFieldAndSetterInjector.class,
				new AnnotatedInjectionPoint(Inject.class));

		// then
		assertThat(sutDescriptor).isNotNull();
		assertThat(sutDescriptor.getSystemUnderTest()).isNotNull();

		ClassWithFieldAndSetterInjector sut = sutDescriptor.getSystemUnderTest();

		assertThat(sut.getFieldInjectionPoint()).isNotNull().isInstanceOf(
				Object.class);
		assertThat(sut.getNotMeantForInjection()).isNull();

		assertThat(sut.getUsedBySetterInjector()).isNotNull();
		assertThat(sut.getUsedByPrivateSetterInjector()).isNotNull();
	}
}
