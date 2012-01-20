package com.googlecode.mockarro;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.inject.Inject;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.googlecode.mockarro.testclasses.ClassWithAllTypesOfInjectionElements;
import com.googlecode.mockarro.testclasses.ClassWithFieldAndSetterInjectionElements;

public class ReflectionInjectionEngineTest {

	@Mock
	private MockEngine mockedMockEngine;

	private InjectionEngine engine;

	@BeforeMethod
	public void init() {
		initMocks(this);
		engine = new InjectionEngine(mockedMockEngine);
	}

	@Test
	public void testNoArgumentsCtorIsUsedAndFieldsAreSettersInjectionElementsAreUsed() {
		// given
		when(mockedMockEngine.createMock(Object.class))
				.thenReturn(new Object()).thenReturn(new Object())
				.thenReturn(new Object());

		// when
		SutDescriptor<ClassWithFieldAndSetterInjectionElements> sutDescriptor = engine
				.createAndInject(
						ClassWithFieldAndSetterInjectionElements.class,
						new AnnotatedInjectionPoint(Inject.class));

		// then
		assertThat(sutDescriptor).isNotNull();
		assertThat(sutDescriptor.getSystemUnderTest()).isNotNull();

		ClassWithFieldAndSetterInjectionElements sut = sutDescriptor
				.getSystemUnderTest();

		assertThat(sut.getFieldInjectionPoint()).isNotNull().isInstanceOf(
				Object.class);
		assertThat(sut.getNotMeantForInjection()).isNull();

		assertThat(sut.getUsedBySetterInjector()).isNotNull();
		assertThat(sut.getUsedByPrivateSetterInjector()).isNotNull();
	}

	@Test
	public void testAllInjectionElementsArePopulated() {
		// given
		when(mockedMockEngine.createMock(Object.class))
				.thenReturn(new Object());
		engine = new InjectionEngine(mockedMockEngine);

		// when
		SutDescriptor<ClassWithAllTypesOfInjectionElements> sutDescriptor = engine
				.createAndInject(ClassWithAllTypesOfInjectionElements.class,
						new AnnotatedInjectionPoint(Inject.class));

		// then
		assertThat(sutDescriptor).isNotNull();
		assertThat(sutDescriptor.getMocks()).hasSize(5);

		ClassWithAllTypesOfInjectionElements sut = sutDescriptor
				.getSystemUnderTest();

		assertThat(sut.getFieldInjectionPoint()).isNotNull().isInstanceOf(
				Object.class);
		assertThat(sut.getNotMeantForInjection()).isNull();
		assertThat(sut.getUsedBySetterInjector()).isNotNull();
		assertThat(sut.getUsedByPrivateSetterInjector()).isNotNull();

		assertThat(sut.getFirstArgSetByCtor()).isNotNull();
		assertThat(sut.getSecondArgSetByCtor()).isNotNull();
	}
}
