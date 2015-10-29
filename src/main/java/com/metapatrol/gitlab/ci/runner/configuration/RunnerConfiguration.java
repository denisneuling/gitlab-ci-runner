package com.metapatrol.gitlab.ci.runner.configuration;

import com.metapatrol.gitlab.ci.runner.etc.annotation.Default;

import java.util.List;
import java.util.Set;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class RunnerConfiguration {

    private String id;
    private String token;
    private String url;
    private String description;
    private List<String> tags;

    private String dockerUsername;
    private String dockerPassword;

    @Default("1")
    private Integer parallelBuilds;

    @Default("busybox")
    private String defaultDockerBuildImage;

    @Default("unix:///var/run/docker.sock")
    private String dockerURI;

    @Default("https://index.docker.io/v1/")
    private String dockerRegistryURL;

    private Set<String> dockerVolumes;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getTags() {
        return tags;
    }

    public Integer getParallelBuilds() {
        return parallelBuilds;
    }

    public void setParallelBuilds(Integer parallelBuilds) {
        this.parallelBuilds = parallelBuilds;
    }

    public String getDefaultDockerBuildImage() {
        return defaultDockerBuildImage;
    }

    public void setDefaultDockerBuildImage(String defaultDockerBuildImage) {
        this.defaultDockerBuildImage = defaultDockerBuildImage;
    }

    public String getDockerUsername() {
        return dockerUsername;
    }

    public void setDockerUsername(String dockerUsername) {
        this.dockerUsername = dockerUsername;
    }

    public String getDockerPassword() {
        return dockerPassword;
    }

    public void setDockerPassword(String dockerPassword) {
        this.dockerPassword = dockerPassword;
    }

    public String getDockerRegistryURL() {
        return dockerRegistryURL;
    }

    public void setDockerRegistryURL(String dockerRegistryURL) {
        this.dockerRegistryURL = dockerRegistryURL;
    }

    public String getDockerURI() {
        return dockerURI;
    }

    public void setDockerURI(String dockerURI) {
        this.dockerURI = dockerURI;
    }

    public Set<String> getDockerVolumes() {
        return dockerVolumes;
    }

    public void setDockerVolumes(Set<String> dockerVolumes) {
        this.dockerVolumes = dockerVolumes;
    }
}
