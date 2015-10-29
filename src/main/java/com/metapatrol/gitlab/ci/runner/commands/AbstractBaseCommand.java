package com.metapatrol.gitlab.ci.runner.commands;

import com.beust.jcommander.Parameter;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public abstract class AbstractBaseCommand {

    @Parameter(names = {"-h","--help"}, description = "Help", help = true)
    private boolean help;

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }
}
