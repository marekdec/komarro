package com.googlecode.komarro.integration;

import static com.googlecode.komarro.Komarro.instanceForTesting;
import static com.googlecode.komarro.MockDescriptor.mockedObject;

import org.testng.annotations.Test;

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
}
