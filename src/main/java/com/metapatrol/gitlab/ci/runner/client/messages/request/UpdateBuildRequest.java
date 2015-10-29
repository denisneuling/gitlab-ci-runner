package com.metapatrol.gitlab.ci.runner.client.messages.request;

import com.google.gson.annotations.SerializedName;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Body;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Method;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Path;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.PathVariable;
import com.metapatrol.gitlab.ci.runner.client.messages.common.HttpMethod;
import com.metapatrol.gitlab.ci.runner.client.messages.payload.api.Payload;
import com.metapatrol.gitlab.ci.runner.client.messages.request.api.Request;
import com.metapatrol.gitlab.ci.runner.client.messages.response.UpdateBuildResponse;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Method(HttpMethod.PUT)
@Path("/api/v1/builds/${id}")
public class UpdateBuildRequest extends Request<UpdateBuildResponse> {

    @PathVariable("${id}")
    private String id;

    @Body
    public UpdateBuildPayload updateBuildPayload = new UpdateBuildPayload();

    public String getState() {
        return updateBuildPayload.getState();
    }

    public void setState(String state) {
        this.updateBuildPayload.setState(state);
    }

    public String getTrace() {
        return updateBuildPayload.getTrace();
    }

    public void setTrace(String trace) {
        this.updateBuildPayload.setTrace(trace);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return updateBuildPayload.getToken();
    }

    public void setToken(String token) {
        this.updateBuildPayload.setToken(token);
    }

    public static class UpdateBuildPayload extends Payload{
        @SerializedName("token")
        private String token;
        @SerializedName("trace")
        private String trace;
        @SerializedName("state")
        private String state;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getTrace() {
            return trace;
        }

        public void setTrace(String trace) {
            this.trace = trace;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
