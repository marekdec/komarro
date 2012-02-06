package com.googlecode.komarro.testclasses;

import javax.inject.Inject;

public class ClassWithAllTypesOfInjectionElements extends
		ClassWithFieldAndSetterInjectionElements {

	private final Object firstArgSetByCtor;
	private final Object secondArgSetByCtor;

	@Inject
	public ClassWithAllTypesOfInjectionElements(Object firstParameter,
			Object secondParameter) {
		this.firstArgSetByCtor = firstParameter;
		this.secondArgSetByCtor = secondParameter;
	}

	public Object getFirstArgSetByCtor() {
		return firstArgSetByCtor;
	}

	public Object getSecondArgSetByCtor() {
		return secondArgSetByCtor;
	}

}
