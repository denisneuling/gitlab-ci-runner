package com.metapatrol.gitlab.ci.runner.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Parameters(commandNames={""})
public class GitlabCIRunnerCommand extends AbstractBaseCommand {

    @Parameter(names = {"-v","--version"}, description = "Version")
    private boolean version;

    public boolean isVersion() {
        return version;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }
}
