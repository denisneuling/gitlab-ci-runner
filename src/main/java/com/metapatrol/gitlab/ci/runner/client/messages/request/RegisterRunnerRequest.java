package com.metapatrol.gitlab.ci.runner.client.messages.request;

import com.google.gson.annotations.SerializedName;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Body;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Method;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Path;
import com.metapatrol.gitlab.ci.runner.client.messages.common.HttpMethod;
import com.metapatrol.gitlab.ci.runner.client.messages.payload.api.Payload;
import com.metapatrol.gitlab.ci.runner.client.messages.request.api.Request;
import com.metapatrol.gitlab.ci.runner.client.messages.response.RegisterRunnerResponse;

import java.util.List;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Path("/api/v1/runners/register.json")
@Method(HttpMethod.POST)
public class RegisterRunnerRequest extends Request<RegisterRunnerResponse> {

    @Body
    private RegisterRunnerRequestPayload registerRunnerRequestPayload = new RegisterRunnerRequestPayload();

    public String getToken() {
        return this.registerRunnerRequestPayload.getToken();
    }

    public void setToken(String token) {
        this.registerRunnerRequestPayload.setToken(token);
    }

    public String getDescription() {
        return this.registerRunnerRequestPayload.getDescription();
    }

    public void setDescription(String description) {
        this.registerRunnerRequestPayload.setDescription(description);
    }

    public List<String> getTagList() {
        return this.registerRunnerRequestPayload.getTagList();
    }

    public void setTagList(List<String> tagList) {
        this.registerRunnerRequestPayload.setTagList(tagList);
    }

    public static class RegisterRunnerRequestPayload extends Payload {
        @SerializedName("token")
        private String token;

        @SerializedName("description")
        private String description;

        @SerializedName("tag_list")
        private List<String> tagList;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getTagList() {
            return tagList;
        }

        public void setTagList(List<String> tagList) {
            this.tagList = tagList;
        }
    }
}
