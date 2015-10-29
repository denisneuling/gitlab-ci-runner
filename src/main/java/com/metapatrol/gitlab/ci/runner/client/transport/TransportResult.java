package com.metapatrol.gitlab.ci.runner.client.transport;

/**
 * <p>TransportResult interface.</p>
 *
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public interface TransportResult {

	/**
	 * <p>getStatusCode.</p>
	 *
	 * @return a int.
	 */
	public int getStatusCode();
	/**
	 * <p>getResult.</p>
	 *
	 * @return a {@link String} object.
	 */
	public String getResult();
}
