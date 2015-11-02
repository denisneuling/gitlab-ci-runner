package com.metapatrol.gitlab.ci.runner.client.messages.payload.response;

import com.google.gson.annotations.SerializedName;
import com.metapatrol.gitlab.ci.runner.client.messages.payload.api.Payload;

import java.util.List;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class RegisterBuildResponsePayload extends Payload {
    /*
    {
       "id":9,
       "commands":"bundle_install\nexecute-script-for-job1",
       "ref":"master",
       "sha":"34431a10ff5b460bc5b738e0481bcfe6636bdf5d",
       "status":"running",
       "project_id":3,
       "repo_url":"http://gitlab-ci-token:96605cb298599a0e39ecbee80cf26c@lab.metapatrol.com/metapatrol/gitlab-ci-runner.git",
       "before_sha":"0000000000000000000000000000000000000000",
       "allow_git_fetch":true,
       "project_name":"metapatrol / gitlab-ci-runner",
       "options":{
          "image":"ruby:2.1",
          "services":[
             "postgres"
          ]
       },
       "timeout":3600,
       "variables":[
          {
             "key":"CI_BUILD_NAME",
             "value":"job1",
             "public":true
          },
          {
             "key":"CI_BUILD_STAGE",
             "value":"build",
             "public":true
          }
       ]
    }
    */

    @SerializedName("id")
    private String id;
    @SerializedName("commands")
    private String commands;
    @SerializedName("ref")
    private String ref;
    @SerializedName("sha")
    private String sha;
    @SerializedName("status")
    private String status;
    @SerializedName("project_id")
    private String projectId;
    @SerializedName("repo_url")
    private String repositoryURL;
    @SerializedName("before_sha")
    private String beforeSHA;
    @SerializedName("allow_git_fetch")
    private Boolean allowGitFetch;
    @SerializedName("project_name")
    private String projectName;
    @SerializedName("timeout")
    private Long timeout;
    @SerializedName("options")
    private Options options;
    @SerializedName("variables")
    private List<Variable> variables;

    public Boolean getAllowGitFetch() {
        return allowGitFetch;
    }

    public void setAllowGitFetch(Boolean allowGitFetch) {
        this.allowGitFetch = allowGitFetch;
    }

    public String getBeforeSHA() {
        return beforeSHA;
    }

    public void setBeforeSHA(String beforeSHA) {
        this.beforeSHA = beforeSHA;
    }

    public String getCommands() {
        return commands;
    }

    public void setCommands(String commands) {
        this.commands = commands;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getRepositoryURL() {
        return repositoryURL;
    }

    public void setRepositoryURL(String repositoryURL) {
        this.repositoryURL = repositoryURL;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    public static class Options {
        @SerializedName("image")
        private String image;
        @SerializedName("services")
        private List<String> services;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public List<String> getServices() {
            return services;
        }

        public void setServices(List<String> services) {
            this.services = services;
        }
    }

    public static class Variable{
        @SerializedName("key")
        private String key;
        @SerializedName("value")
        private String value;
        @SerializedName("public")
        private Boolean _public;

        public Variable(){}
        public Variable(String key, String value){
            this._public = true;
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Boolean getPublic() {
            return _public;
        }

        public void setPublic(Boolean aPublic) {
            _public = aPublic;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
