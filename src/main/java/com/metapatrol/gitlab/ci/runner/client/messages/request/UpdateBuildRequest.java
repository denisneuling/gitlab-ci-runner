package com.metapatrol.gitlab.ci.runner.client.messages.request;

import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Body;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Method;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Path;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.PathVariable;
import com.metapatrol.gitlab.ci.runner.client.messages.common.HttpMethod;
import com.metapatrol.gitlab.ci.runner.client.messages.payload.constants.BuildState;
import com.metapatrol.gitlab.ci.runner.client.messages.payload.request.UpdateBuildRequestPayload;
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
    public UpdateBuildRequestPayload updateBuildRequestPayload = new UpdateBuildRequestPayload();

    public BuildState getState() {
        return updateBuildRequestPayload.getState();
    }

    public void setState(BuildState state) {
        this.updateBuildRequestPayload.setState(state);
    }

    public String getTrace() {
        return updateBuildRequestPayload.getTrace();
    }

    public void setTrace(String trace) {
        this.updateBuildRequestPayload.setTrace(trace);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return updateBuildRequestPayload.getToken();
    }

    public void setToken(String token) {
        this.updateBuildRequestPayload.setToken(token);
    }
}
