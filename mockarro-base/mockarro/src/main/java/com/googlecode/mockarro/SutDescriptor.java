package com.googlecode.mockarro;

import java.util.Set;

final class SutDescriptor<T> {

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
