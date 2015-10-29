package com.metapatrol.gitlab.ci.runner.client.messages.payload.api;

public abstract class Payload {
	protected String entityClassName = this.getClass().getName();

	public Payload(){}

	protected String getEntityClassName() {
		return entityClassName;
	}
}
