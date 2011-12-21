package com.googlecode.mockarro.injector;

import java.util.Set;

public class SutDescriptor<T> {

	private final Set<MockDescriptor> mocks;

	private final T systemUnderTest;

	public Set<MockDescriptor> getMocks() {
		return mocks;
	}

	public T getSystemUnderTest() {
		return systemUnderTest;
	}

	public SutDescriptor(Set<MockDescriptor> mocks, T systemUnderTest) {
		super();
		this.mocks = mocks;
		this.systemUnderTest = systemUnderTest;
	}
}
