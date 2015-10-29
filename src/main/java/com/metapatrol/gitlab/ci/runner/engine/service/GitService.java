package com.metapatrol.gitlab.ci.runner.engine.service;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Service
public class GitService {

    private Credentials credentialsFromRepositoryURL(String repositoryURL) throws MalformedURLException {
        try {
            URL reference = new URL(repositoryURL);
            String[] userinfo = reference.getUserInfo().split(":"); // "user:password"
            return new Credentials(userinfo[0], userinfo[1]);
        }catch(Throwable throwable){
            return new Credentials("", "");
        }
    }

    private TransportConfigCallback transportConfigCallback(String url) throws MalformedURLException {
        final Credentials credentials = credentialsFromRepositoryURL(url);

        return new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                transport.setCredentialsProvider(new UsernamePasswordCredentialsProvider(credentials.getUsername(), credentials.getPassword()));
            }
        };
    }

    public Git getGitFromPath(File file) throws IOException {
        return Git.open(file);
    }

    public Ref checkout(Git git, String sha1) throws IOException, GitAPIException {
        Ref ref = git
            .checkout()
            .setForce(true)
            .setName(sha1)
            .call();

        return ref;
    }

    public PullResult pull(String repositoryURL, String refBranch, Git git) throws GitAPIException, MalformedURLException {
        return pull(repositoryURL, refBranch, git, new DumbProgressStateListener());
    }
    public PullResult pull(String repositoryURL, String refBranch, Git git, ProgressStateListener progressStateListener) throws GitAPIException, MalformedURLException {
        PullResult pullResult = git
            .pull()
            .setProgressMonitor(new SimpleProgressMonitor(progressStateListener))
            .setRemoteBranchName(refBranch)
            .setTransportConfigCallback(
                    transportConfigCallback(repositoryURL)
            )
            .call();

        return pullResult;
    }

    public void fetch(String repositoryURL, Git git) throws GitAPIException, MalformedURLException {
        fetch(repositoryURL, git, new DumbProgressStateListener());
    }
    public void fetch(String repositoryURL, Git git, ProgressStateListener progressStateListener) throws GitAPIException, MalformedURLException {
        try {
            git
                .fetch()
                .setTagOpt(TagOpt.FETCH_TAGS)
                .setRemoveDeletedRefs(true)
                .setDryRun(false)
                .setProgressMonitor(new SimpleProgressMonitor(progressStateListener))
                .setTransportConfigCallback(
                        transportConfigCallback(repositoryURL)
                )
                .call();
        }catch(TransportException transportException){
            if(transportException.getMessage()!=null && transportException.getMessage().equals("Nothing to fetch.")){
                // up to date
            }else{
                throw transportException;
            }
        }
    }

    public Git cloneRepository(String repositoryURL, File target, boolean bare) throws GitAPIException, IOException {
        return cloneRepository(repositoryURL, target, bare, new DumbProgressStateListener());
    }
    public Git cloneRepository(String repositoryURL, File target, boolean bare, ProgressStateListener progressStateListener) throws GitAPIException, IOException {
        Git git = Git.cloneRepository()
            .setURI(repositoryURL)
            .setBare(bare)
            .setProgressMonitor(new SimpleProgressMonitor(progressStateListener))
            .setTransportConfigCallback(transportConfigCallback(repositoryURL))
            .setDirectory(target)
            .call();

        return git;
    }

    private static class DumbProgressStateListener extends ProgressStateListener {
        private Logger log = Logger.getLogger(getClass());
        @Override
        public void enterState(State state) {
            if(log.isDebugEnabled()){
                log.debug(state.getStream()+"|"+state.getMessage());
            }
        }
    }

    private static class SimpleProgressMonitor implements ProgressMonitor {
        private ProgressStateListener progressStateListener;

        SimpleProgressMonitor(ProgressStateListener progressStateListener){
            this.progressStateListener = progressStateListener;
        }

        @Override
        public void start(int totalTasks) {

        }

        @Override
        public void beginTask(String title, int totalWork) {
            progressStateListener.enterState(new ProgressStateListener.State(ProgressStateListener.State.Stream.STDOUT, title));
        }

        @Override
        public void update(int completed) {

        }

        @Override
        public void endTask() {
        }

        @Override
        public boolean isCancelled() {
            return false;
        }
    }

    static class Credentials {
        private final String username;
        private final String password;

        public Credentials(String username, String password){
            this.username = username;
            this.password = password;
        }

        public String getPassword() {
            return password;
        }

        public String getUsername() {
            return username;
        }
    }
}
