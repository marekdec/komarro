package com.googlecode.komarro;

import static org.fest.assertions.Assertions.assertThat;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.googlecode.komarro.AnnotatedInjectionPoint;
import com.googlecode.komarro.testclasses.ClassWithFieldAndSetterInjectionElements;

public class AnnotatedInjectionPointTest {

	private final Class<ClassWithFieldAndSetterInjectionElements> dummy = ClassWithFieldAndSetterInjectionElements.class;

	@Test
	public void verifyElementsAreProperlyQualified() throws SecurityException,
			NoSuchFieldException, NoSuchMethodException {
		final AnnotatedInjectionPoint ip = new AnnotatedInjectionPoint(
				Inject.class);

		assertThat(
				ip.isInjectable(dummy.getDeclaredField("fieldInjectionPoint")))
				.isTrue();
		assertThat(
				ip.isInjectable(dummy.getDeclaredField("notMeantForInjection")))
				.isFalse();

		assertThat(ip.isInjectable(dummy.getDeclaredConstructor())).isFalse();
		assertThat(ip.isInjectable(dummy.getDeclaredConstructor(Object.class)))
				.isTrue();

		assertThat(
				ip.isInjectable(dummy.getDeclaredMethod(
						"setUsedByPrivateSetterInjector", Object.class)))
				.isTrue();
		assertThat(
				ip.isInjectable(dummy.getDeclaredMethod(
						"setUsedBySetterInjector", Object.class))).isTrue();
		assertThat(
				ip.isInjectable(dummy.getDeclaredMethod("regularMethod",
						Object.class))).isFalse();
	}
}
