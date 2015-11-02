package com.metapatrol.gitlab.ci.runner.client.messages.payload.request;

import com.google.gson.annotations.SerializedName;
import com.metapatrol.gitlab.ci.runner.client.messages.payload.api.Payload;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class RegisterBuildRequestPayload extends Payload{
    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
