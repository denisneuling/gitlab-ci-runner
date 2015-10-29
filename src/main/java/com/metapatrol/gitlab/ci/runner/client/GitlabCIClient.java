package com.metapatrol.gitlab.ci.runner.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.metapatrol.gitlab.ci.runner.client.messages.normalize.Normalizer;
import com.metapatrol.gitlab.ci.runner.client.messages.request.api.Request;
import com.metapatrol.gitlab.ci.runner.client.messages.response.api.Response;
import com.metapatrol.gitlab.ci.runner.client.transport.TransportFactory;
import com.metapatrol.gitlab.ci.runner.client.transport.exception.TransportException;
import com.metapatrol.gitlab.ci.runner.client.util.NormalizationUtil;
import com.metapatrol.gitlab.ci.runner.client.util.RequestUtil;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;

public class GitlabCIClient extends AbstractGitlabCIClient {
    protected Logger log = Logger.getLogger(getClass());


    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

    private URL url;
    private String token;

    public GitlabCIClient(URL url) {
        this(url, null);
    }
    public GitlabCIClient(String url) {
        this(url, null);
    }

    public GitlabCIClient(URL url, String token) {
        transportFactory = new TransportFactory();

        this.token = token;
        this.url = url;
    }
    public GitlabCIClient(String url, String token) {
        transportFactory = new TransportFactory();

        this.token = token;

        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            // can't happen
            throw new RuntimeException(e);
        }

    }

    @Override
    public URL getURL() {
        return url;
    }

    public void setURL(URL url) {
        this.url = url;
    }

    /**
     * <p>
     * send.
     * </p>
     *
     *                object.
     * @param <T>     a T object.
     * @return a T object.
     */
    @SuppressWarnings("unchecked")
    public <T extends Response<?>> T send(Request<T> request) {
        if (request == null) {
            throw new TransportException("Request cannot be null.");
        }

        request.setPrivateToken(token);

        RequestUtil.validate(request);
        T response = dispatchByMethod(request);
        return (T) marshal(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T extends Response<?>> T deserialize(String response, Request<T> request) {
        T target = (T) RequestUtil.getInstanceOfParameterizedType(request);

        Normalizer normalizer = NormalizationUtil.getNormalizer(target);
        response = normalizer.normalize(response);

        target.setResult(response);
        return target;
    }

    @SuppressWarnings("unchecked")
    protected <T extends Response<?>> T marshal(T response) {
        T toReturn = response;

        try {
            toReturn = (T) gson.fromJson(response.getResult(), response.getClass());
            toReturn.setResponseTime(response.getResponseTime());
            toReturn.setStatusMessage(response.getStatusMessage());
            toReturn.setStatusCode(response.getStatusCode());
            toReturn.setResult(response.getResult());
        } catch (final Throwable throwable) {
            final T failedResponse = response;
            log.warn("Scheme is not up to date for type " + failedResponse.getClass().getName(), throwable);
            return response;
        }
        return toReturn;
    }

    /*
    public static void main(String[] args){
        Handler fh = new ConsoleHandler();
        fh.setLevel (Level.ALL);
        Logger logger = Logger.getLogger("com");
        logger.addHandler (fh);

        GitlabCIClient cl = new GitlabCIClient("http://ci.metapatrol.com", "http://lab.metapatrol.com", "BJ_5EREoga-vD6Qz9tdC");

        ListProjectRequest reqP = new ListProjectRequest();
        ListProjectResponse resP = cl.send(reqP);

        ListPayload<Project> projects = resP.getPayload();
        System.out.println(projects);
        if(projects!=null){
            for(Project project : projects){
                System.out.println(project);
                System.out.println(cl.send(new DetailsProjectRequest(project.getId())).getResult());
                ListProjectCommitRequest lpcr = new ListProjectCommitRequest();
                lpcr.setProjectId(project.getId());
                lpcr.setProjectToken(project.getToken());
                System.out.println(cl.send(lpcr).getResult());
            }
        }


        ListRunnerRequest reqR = new ListRunnerRequest();
        ListRunnerResponse resR = cl.send(reqR);
        ListPayload<Runner> runners = resR.getPayload();
        if(runners!=null){
            for(Runner runner : runners){
                System.out.println(runner);
            }
        }
    }
    */
}
