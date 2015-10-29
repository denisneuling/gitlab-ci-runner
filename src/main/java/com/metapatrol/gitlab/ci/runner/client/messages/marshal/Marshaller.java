package com.metapatrol.gitlab.ci.runner.client.messages.marshal;

import com.metapatrol.gitlab.ci.runner.client.messages.marshal.exception.MarshallingException;
import com.metapatrol.gitlab.ci.runner.client.messages.marshal.exception.UnmarshallingException;

public abstract class Marshaller<IN, OUT> {

	public Marshaller(){}

	public abstract IN marshal(OUT in) throws MarshallingException;

	public abstract OUT unmarshal(IN out) throws UnmarshallingException;

}
