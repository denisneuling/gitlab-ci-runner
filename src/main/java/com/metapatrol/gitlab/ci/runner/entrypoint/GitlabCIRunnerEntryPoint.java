package com.metapatrol.gitlab.ci.runner.entrypoint;

import com.metapatrol.gitlab.ci.runner.commands.GitlabCIRunnerCommand;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class GitlabCIRunnerEntryPoint extends EntryPoint<GitlabCIRunnerCommand> {
    @Override
    public boolean enter(GitlabCIRunnerCommand command) {
        return false;
    }
}
