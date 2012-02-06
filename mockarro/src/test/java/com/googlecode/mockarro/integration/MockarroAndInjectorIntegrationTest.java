package com.googlecode.mockarro.integration;

import static com.googlecode.mockarro.MockDescriptor.mockedObject;
import static com.googlecode.mockarro.Mockarro.instanceForTesting;
import static com.googlecode.mockarro.MockitoMockDescriptionCreator.annotatedMocks;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import org.mockito.Mock;
import org.testng.annotations.Test;

import com.googlecode.mockarro.testclasses.MultiplierService;
import com.googlecode.mockarro.testclasses.SystemUnderTest;

public class MockarroAndInjectorIntegrationTest {

	@Mock
	private MultiplierService multService;

	@Test
	public void verifyInitSutWithMocksInjectsGivenMocks() {
		// given
		final MultiplierService multiplierService = new MultiplierService();

		// when
		SystemUnderTest sut = instanceForTesting(SystemUnderTest.class,
				mockedObject(multiplierService).ofType(MultiplierService.class));

		// then
		assertThat(sut.getService()).isSameAs(multiplierService);
	}

	@Test
	public void verifyInitSutWithMocksInjectsDiscoveredMocks() {
		// given
		initMocks(this);

		// when
		SystemUnderTest sut = instanceForTesting(SystemUnderTest.class,
				annotatedMocks(this));

		// then
		assertThat(sut.getService()).isSameAs(multService);
	}
}
