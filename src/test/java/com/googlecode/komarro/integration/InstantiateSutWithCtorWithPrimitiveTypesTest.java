package com.googlecode.komarro.integration;

import static com.googlecode.komarro.Komarro.instanceForTesting;
import static com.googlecode.komarro.MockDescriptor.mockedObject;
import static org.fest.assertions.Assertions.assertThat;

import org.testng.annotations.Test;

import com.googlecode.komarro.testclasses.SUTWithAllPrimitveTypesToInject;
import com.googlecode.komarro.testclasses.SUTWithCollaboratorThatCannotBeMocked;
import com.googlecode.komarro.testclasses.SUTWithCtorWithPrimitveTypes;

public class InstantiateSutWithCtorWithPrimitiveTypesTest {

	/**
	 * This verifies the fix to the Issue#2: when a MockDescriptor without a
	 * name was given, the injection engine failed to inject the objects
	 * properly. The name parameter is optional and should not be necessary for
	 * the correct behavior of the injection engine.
	 */
	@Test
	public void mockedObjectWithNoNameCanBePassedOnInit() {
		instanceForTesting(SUTWithCtorWithPrimitveTypes.class,
				mockedObject("A string").ofType(String.class), mockedObject(7)
						.ofType(int.class));
	}

	/**
	 * This test verifies the fix to the Issue#1: the error message when a
	 * collaborator cannot be mocked has to be more descriptive about the
	 * reason.
	 */
	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*cannot be created.*")
	public void errorMessageWhenFailingToCreateMockIsDescriptive() {
		// when
		instanceForTesting(SUTWithCollaboratorThatCannotBeMocked.class);

		// then expect an exception
	}

	@Test
	public void verifyCorrectDefaultValuesAreGivenToInjectedPrimitiveTypes() {
		SUTWithAllPrimitveTypesToInject sut = instanceForTesting(SUTWithAllPrimitveTypesToInject.class);

		assertThat(sut.getByteParam()).isEqualTo((byte) 0);
		assertThat(sut.getShortParam()).isEqualTo((short) 0);
		assertThat(sut.getIntParam()).isEqualTo(0);
		assertThat(sut.getLongParam()).isEqualTo(0L);
		assertThat(sut.getDoubleParam()).isEqualTo(0.0);
		assertThat(sut.getFloatParam()).isEqualTo((float) 0.0);
		assertThat(sut.isBooleanParam()).isEqualTo(false);
		assertThat(sut.getCharParam()).isEqualTo((char) 0);
		assertThat(sut.getStringParam()).isEqualTo("");
	}
}
