package com.googlecode.komarro.testclasses;

import javax.inject.Inject;

public class SUTWithCollaboratorThatCannotBeMocked {

	private ClassThatCannotBeMocked collaborator;

	public ClassThatCannotBeMocked getCollaborator() {
		return collaborator;
	}

	@Inject
	public void setCollaborator(ClassThatCannotBeMocked collaborator) {
		this.collaborator = collaborator;
	}
}
