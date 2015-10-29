package com.metapatrol.gitlab.ci.runner.client.messages.payload.request;

import com.google.gson.annotations.SerializedName;
import com.metapatrol.gitlab.ci.runner.client.messages.payload.api.Payload;
import com.metapatrol.gitlab.ci.runner.client.messages.payload.constants.BuildState;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class UpdateBuildRequestPayload extends Payload {
    @SerializedName("token")
    private String token;
    @SerializedName("trace")
    private String trace;
    @SerializedName("state")
    private BuildState state;

    public BuildState getState() {
        return state;
    }

    public void setState(BuildState state) {
        this.state = state;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }
}
