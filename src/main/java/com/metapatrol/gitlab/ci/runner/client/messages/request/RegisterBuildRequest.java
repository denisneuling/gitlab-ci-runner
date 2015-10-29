package com.metapatrol.gitlab.ci.runner.client.messages.request;

import com.google.gson.annotations.SerializedName;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Body;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Method;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Path;
import com.metapatrol.gitlab.ci.runner.client.messages.common.HttpMethod;
import com.metapatrol.gitlab.ci.runner.client.messages.payload.api.Payload;
import com.metapatrol.gitlab.ci.runner.client.messages.request.api.Request;
import com.metapatrol.gitlab.ci.runner.client.messages.response.RegisterBuildResponse;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Path("/api/v1/builds/register.json")
@Method(HttpMethod.POST)
public class RegisterBuildRequest extends Request<RegisterBuildResponse> {

    @Body
    private RegisterBuildRequestPayload registerBuildRequestPayload = new RegisterBuildRequestPayload();

    public String getToken() {
        return this.registerBuildRequestPayload.getToken();
    }

    public void setToken(String token) {
        this.registerBuildRequestPayload.setToken(token);
    }

    public static class RegisterBuildRequestPayload extends Payload {
        @SerializedName("token")
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
