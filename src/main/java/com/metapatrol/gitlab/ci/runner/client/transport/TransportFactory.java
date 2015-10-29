package com.metapatrol.gitlab.ci.runner.client.transport;

import com.metapatrol.gitlab.ci.runner.client.transport.exception.TransportException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class TransportFactory {

	protected List<Class<Transport>> availableTransports = new LinkedList<Class<Transport>>();

	public TransportFactory(){
		registerTransport("com.metapatrol.gitlab.ci.runner.client.transport.httpclient.HttpClientTransport");
	}

	public final List<Class<Transport>> getAvailableTransports() {
		return availableTransports;
	}

	public Transport newTransport() {
		List<Class<Transport>> knownTransports = getAvailableTransports();
		if (knownTransports.isEmpty()) {
			throw new TransportException("No transport provider available. Is there one on the classpath?");
		}
		return newTransport(knownTransports.get(knownTransports.size() - 1));
	}

	public Transport newTransport(Class<Transport> transportClazz) {
		try {
			Transport transport = transportClazz.newInstance();
			return transport;
		} catch (IllegalAccessException e) {
			throw new TransportException(e);
		} catch (InstantiationException e) {
			throw new TransportException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public final void registerTransport(String transportClazz) {
		if (transportClazz == null) {
			return;
		}
		try {
			registerTransport((Class<Transport>) Class.forName(transportClazz));
		} catch (ClassNotFoundException e) {
			return;
		} catch (ClassCastException cce) {
			throw new TransportException(cce);
		}
	}

	public final void registerTransport(Class<Transport> transportClazz) {
		if (transportClazz == null) {
			return;
		}

		HashSet<Class<Transport>> set = new HashSet<Class<Transport>>(availableTransports);
		set.add(transportClazz);

		availableTransports = new LinkedList<Class<Transport>>(set);
	}
}
