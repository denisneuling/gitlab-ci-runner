package com.metapatrol.gitlab.ci.runner.engine.service;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Service
public class ProjectBuildGitService {
    private Logger log = Logger.getLogger(getClass());

    @Autowired
    private GitService gitService;

    public Git ensureProjectBuildGitDirectory(File projectBuildGitDirectory, String remoteRepositoryURI, String sha, BuildService.GitProgressStateListener progressStateListener) {
        Git git = null;

        try{
            git = gitService.cloneRepository(
                remoteRepositoryURI
            ,   projectBuildGitDirectory
            ,   false // clone bare
            ,   progressStateListener
            );
        } catch (GitAPIException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        try {
            gitService.checkout(git, sha);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        return git;
    }
}
