package com.metapatrol.gitlab.ci.runner.engine.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Service
public class ProjectBuildGitService {

    @Autowired
    private GitService gitService;

    public Git ensureProjectBuildGitDirectory(File projectBuildGitDirectory, String remoteRepositoryURI, String sha, ProgressStateListener progressStateListener) throws IOException, GitAPIException {
        Git git = null;

        git = gitService.cloneRepository(
            remoteRepositoryURI
        ,   projectBuildGitDirectory
        ,   false // clone bare
        ,   progressStateListener
        );


        gitService.checkout(git, sha);

        return git;
    }
}
