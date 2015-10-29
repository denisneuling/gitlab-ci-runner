package com.metapatrol.gitlab.ci.runner.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.net.URL;
import java.util.List;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Parameters(commandNames={"register"})
public class RegisterCommand extends AbstractBaseCommand {

    @Parameter(names = {"-u","--url"}, description = "URL", required = true)
    private URL url;

    @Parameter(names = {"-t","--token"}, description = "Token", required = true)
    private String token;

    @Parameter(names = {"-d","--description"}, description = "Description")
    private String description;

    @Parameter(names = {"-tag","--tag"}, description = "Tags for this Runner")
    private List<String> tags;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
