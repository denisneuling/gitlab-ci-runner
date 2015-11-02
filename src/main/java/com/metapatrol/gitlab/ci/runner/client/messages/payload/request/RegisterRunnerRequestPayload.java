package com.metapatrol.gitlab.ci.runner.client.messages.payload.request;

import com.google.gson.annotations.SerializedName;
import com.metapatrol.gitlab.ci.runner.client.messages.payload.api.Payload;

import java.util.List;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class RegisterRunnerRequestPayload extends Payload {
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
