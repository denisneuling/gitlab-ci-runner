package com.metapatrol.gitlab.ci.runner.entrypoint;

import com.metapatrol.gitlab.ci.runner.commands.StartCommand;
import com.metapatrol.gitlab.ci.runner.engine.Engine;
import org.apache.log4j.Logger;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class StartEntryPoint extends EntryPoint<StartCommand> {
    private Logger log = Logger.getLogger(getClass());

    @Override
    public boolean enter(StartCommand command) {
        log.info("Starting Gitlab CI Runner.");

        new Engine().start();

        return true;
    }
}
