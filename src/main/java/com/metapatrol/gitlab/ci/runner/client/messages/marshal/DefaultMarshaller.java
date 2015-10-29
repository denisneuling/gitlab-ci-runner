package com.metapatrol.gitlab.ci.runner.client.messages.marshal;

public class DefaultMarshaller extends Marshaller<Object, Object>{

	@Override
	public Object marshal(Object object) {
		return object;
	}

	@Override
	public Object unmarshal(Object object) {
		return object;
	}
}
