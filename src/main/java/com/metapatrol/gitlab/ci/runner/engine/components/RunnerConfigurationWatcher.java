package com.metapatrol.gitlab.ci.runner.engine.components;

import com.metapatrol.gitlab.ci.runner.etc.Etc;
import com.metapatrol.gitlab.ci.runner.fs.FileSystem;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
/*@Service*/ // we dont need this
public class RunnerConfigurationWatcher implements InitializingBean {
    private Logger log = Logger.getLogger(getClass());

    @Autowired
    private Etc etc;

    @Autowired
    private FileSystem fileSystem;

    @Autowired
    private RunnerConfigurationProvider runnerConfigurationProvider;

    @Autowired
    private SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;

    @Override
    public void afterPropertiesSet() throws Exception {
        simpleAsyncTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    WatchService watcher = FileSystems.getDefault().newWatchService();
                    WatchKey key = fileSystem.getConfigurationDirectory().toPath().register(
                        watcher
                    ,   StandardWatchEventKinds.ENTRY_MODIFY
                    ,   StandardWatchEventKinds.ENTRY_DELETE
                    ,   StandardWatchEventKinds.ENTRY_CREATE
                    );

                    while (true) {
                        final WatchKey wk = watcher.take();
                        for (WatchEvent<?> event : wk.pollEvents()) {
                            final Path changed = (Path) event.context();
                            if(event.kind().name().equals("ENTRY_MODIFY") && changed.toString().equals(fileSystem.getConfigurationFile().toPath().getFileName().toString())){
                                runnerConfigurationProvider.set(etc.read(runnerConfigurationProvider.get()));
                            }
                        }
                        // reset the key
                        boolean valid = wk.reset();
                    }
                } catch (Exception x) {
                }
            }
        });

    }
}
