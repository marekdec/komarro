package com.googlecode.komarro.testclasses;

import javax.inject.Inject;

public class SUTWithCtorWithPrimitveTypes {

	private final MultiplierService collaborator;

	private final String stringParam;

	private final int intParam;

	@Inject
	public SUTWithCtorWithPrimitveTypes(MultiplierService collaborator,
			String stringParam, int intParam) {
		super();
		this.collaborator = collaborator;
		this.stringParam = stringParam;
		this.intParam = intParam;
	}

	public MultiplierService getCollaborator() {
		return collaborator;
	}

	public String getStringParam() {
		return stringParam;
	}

	public int getIntParam() {
		return intParam;
	}
}
