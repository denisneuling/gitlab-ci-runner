package com.metapatrol.gitlab.ci.runner.client.messages.response;

import com.metapatrol.gitlab.ci.runner.client.messages.payload.api.Payload;
import com.metapatrol.gitlab.ci.runner.client.messages.response.api.Response;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class UpdateBuildResponse extends Response<UpdateBuildResponse.UpdateBuildPayload>{

    public static class UpdateBuildPayload extends Payload {}
}
