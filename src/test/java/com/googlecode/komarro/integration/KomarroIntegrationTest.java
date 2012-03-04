package com.googlecode.komarro.integration;

import static com.googlecode.komarro.Komarro.instanceForTesting;
import static com.googlecode.komarro.MockDescriptor.mockedObject;
import static com.googlecode.komarro.MockitoMockDescriptionCreator.annotatedMocks;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import org.mockito.Mock;
import org.testng.annotations.Test;

import com.googlecode.komarro.MockDescriptor;
import com.googlecode.komarro.testclasses.MultiplierService;
import com.googlecode.komarro.testclasses.SystemUnderTest;

public class KomarroIntegrationTest {

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

	@Test
	public void verifyInitSutWithMocksInjectsDiscoveredAndGivenMocks() {
		// given
		initMocks(this);
		final String nameForService = "A multiplier service";

		// when
		SystemUnderTest sut = instanceForTesting(SystemUnderTest.class,
				annotatedMocks(this),
				mockedObject(nameForService).ofType(String.class));

		// then
		assertThat(sut.getService()).isSameAs(multService);
		assertThat(sut.getName()).isEqualTo(nameForService);
	}

	@Test
	public void verifyInitSutWithMocksInjectsDiscoveredAndNullGivenMocks() {
		// given
		initMocks(this);

		// when
		SystemUnderTest sut = instanceForTesting(SystemUnderTest.class,
				annotatedMocks(this), (MockDescriptor[]) null);

		// then
		assertThat(sut.getService()).isSameAs(multService);
		assertThat(sut.getName()).isEqualTo("");
	}

	// TODO: add a test with no annotated mocks discovered!!
}
