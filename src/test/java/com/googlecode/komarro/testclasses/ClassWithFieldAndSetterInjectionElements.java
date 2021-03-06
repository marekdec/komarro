package com.googlecode.komarro.testclasses;

import javax.inject.Inject;

public class ClassWithFieldAndSetterInjectionElements {

	@Inject
	private Object fieldInjectionPoint;

	private Object notMeantForInjection;

	private Object usedBySetterInjector;

	private Object usedByPrivateSetterInjector;

	// this is meant to be used only by an injection engine
	@SuppressWarnings("unused")
	@Inject
	private void setUsedByPrivateSetterInjector(final Object mock) {
		this.usedByPrivateSetterInjector = mock;
	}

	@Inject
	public void setUsedBySetterInjector(final Object mock) {
		this.usedBySetterInjector = mock;
	}

	public void regularMethod(final Object mock) {
		throw new IllegalStateException("Not meant to be executed.");
	}

	public ClassWithFieldAndSetterInjectionElements() {
		// Intentionally empty
	}

	@Inject
	public ClassWithFieldAndSetterInjectionElements(final Object mock) {
		fieldInjectionPoint = mock;
	}

	public Object getFieldInjectionPoint() {
		return fieldInjectionPoint;
	}

	public Object getNotMeantForInjection() {
		return notMeantForInjection;
	}

	public Object getUsedBySetterInjector() {
		return usedBySetterInjector;
	}

	public Object getUsedByPrivateSetterInjector() {
		return usedByPrivateSetterInjector;
	}
}
