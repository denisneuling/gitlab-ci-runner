package com.metapatrol.gitlab.ci.runner.client.messages.payload.response;

import com.google.gson.annotations.SerializedName;
import com.metapatrol.gitlab.ci.runner.client.messages.payload.api.Payload;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class RunnerPayload extends Payload{

    /*
    {
        "id" : 85,
            "token" : "12b68e90394084703135"
    }
    */

    @SerializedName("id")
    private String id;

    @SerializedName("token")
    private String token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "RegisterRunnerPayload{" +
                "id='" + id + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
