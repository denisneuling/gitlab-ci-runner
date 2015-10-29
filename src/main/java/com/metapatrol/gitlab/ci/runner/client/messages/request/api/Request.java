package com.metapatrol.gitlab.ci.runner.client.messages.request.api;

import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Header;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Query;
import com.metapatrol.gitlab.ci.runner.client.messages.response.api.Response;

import java.io.Serializable;

/**
 * HTTP Request DTO (DTO DataTransferObject)
 *
 * @author Denis Neuling (denisneuling@gmail.com)
 *
 * @param <RESPONSE>
 */
public abstract class Request<RESPONSE extends Response<?>> implements Serializable {
	private static final long serialVersionUID = -4458336211631611916L;

	@Header("PRIVATE-TOKEN")
	private String privateToken;

	@Query("url")
	private String url;

	public String getPrivateToken() {
		return privateToken;
	}

	public void setPrivateToken(String privateToken) {
		this.privateToken = privateToken;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}

