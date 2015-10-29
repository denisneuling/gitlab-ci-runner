package com.metapatrol.gitlab.ci.runner.engine.service;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Service
public class ProjectGitService {
    private Logger log = Logger.getLogger(getClass());

    @Autowired
    private GitService gitService;

    public Git ensureProjectGitDirectory(File projectGitDirectory, String remoteRepositoryURI, ProgressStateListener progressStateListener){
        Git git = null;

        if(projectGitDirectory.exists()) {
            try {
                git = gitService.getGitFromPath(projectGitDirectory);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        boolean fetch = true;
        if(git == null) {
            try {
                git = gitService.cloneRepository(
                    remoteRepositoryURI
                ,   projectGitDirectory
                ,   true // clone bare
                ,   progressStateListener
                );
                fetch = false;
            } catch (GitAPIException e) {
                log.error(e.getMessage(), e);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        if(fetch) {
            try {
                gitService.fetch(
                    remoteRepositoryURI
                ,   git
                ,   progressStateListener
                );
            } catch (GitAPIException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return git;
    }
}
