package com.metapatrol.gitlab.ci.runner.client;

import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Default;
import com.metapatrol.gitlab.ci.runner.client.messages.common.HttpMethod;
import com.metapatrol.gitlab.ci.runner.client.messages.common.HttpStatus;
import com.metapatrol.gitlab.ci.runner.client.messages.common.Timer;
import com.metapatrol.gitlab.ci.runner.client.messages.request.api.Request;
import com.metapatrol.gitlab.ci.runner.client.messages.response.api.Response;
import com.metapatrol.gitlab.ci.runner.client.transport.Transport;
import com.metapatrol.gitlab.ci.runner.client.transport.TransportResult;
import com.metapatrol.gitlab.ci.runner.client.transport.exception.TransportException;
import com.metapatrol.gitlab.ci.runner.client.transport.TransportFactory;
import com.metapatrol.gitlab.ci.runner.client.util.HttpMethodUtil;
import com.metapatrol.gitlab.ci.runner.client.util.PathUtil;
import com.metapatrol.gitlab.ci.runner.client.util.RequestUtil;
import org.apache.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * Created by ska on 03.01.15.
 */
public abstract class AbstractGitlabCIClient {
    protected final Logger log = Logger.getLogger(getClass());

    protected TransportFactory transportFactory;

    public TransportFactory getTransportFactory() {
        if (transportFactory == null) {
            transportFactory = new TransportFactory();
        }
        return transportFactory;
    }

    public void setTransportFactory(TransportFactory transportFactory) {
        this.transportFactory = transportFactory;
    }

    /**
     * <p>
     * dispatchByMethod.
     * </p>
     *
     * @param request
     *            object.
     * @param <T>
     *            a T object.
     * @return a {@link Response}
     *         object.
     */
    protected <T extends Response<?>> T dispatchByMethod(Request<T> request) {
        HttpMethod method = HttpMethodUtil.retrieveMethod(request);

        switch (method) {
            case GET:
                return doGet(request);
            case POST:
                return doPost(request);
            case PUT:
                return doPut(request);
            case DELETE:
                return doDelete(request);
            default:
                throw new TransportException(HttpStatus.Not_Implemented.toString());
        }
    }

    /**
     * <p>
     * doGet.
     * </p>
     *
     * @param request
     *            a {@link Request}
     *            object.
     * @param <T>
     *            a T object.
     * @return a {@link Response}
     *         object.
     */
    protected <T extends Response<?>> T doGet(Request<T> request) {
        URI uri = buidURI(request);

        Map<String, String> headers = RequestUtil.resolveHeaders(request);

        Timer timer = Timer.tic();
        Transport transport = getTransportFactory().newTransport();
        TransportResult result = transport.doGet(uri, headers, request);
        timer.toc();

        if (log.isDebugEnabled()) {
            log.debug(result.getStatusCode() + " " + uri.toString() + " took " + timer.getDifference() + "ms");
        }

        T response = toResponse(timer, result, request);

        return response;
    }

    /**
     * <p>
     * doPost.
     * </p>
     *
     * @param request
     *            a {@link Request}
     *            object.
     * @param <T>
     *            a T object.
     * @return a {@link Response}
     *         object.
     */
    protected <T extends Response<?>> T doPost(Request<T> request) {
        String payload = RequestUtil.getRequestPayload(request);

        if(log.isDebugEnabled()) {
            log.debug(payload);
        }
        URI uri = buidURI(request);

        Map<String, String> headers = RequestUtil.resolveHeaders(request);

        Timer timer = Timer.tic();

        Transport transport = getTransportFactory().newTransport();

        TransportResult result = transport.doPost(uri, headers, payload, request);
        timer.toc();

        if (log.isDebugEnabled()) {
            log.debug(result.getStatusCode() + " " + uri.toString() + " took " + timer.getDifference() + "ms");
        }

        T response = toResponse(timer, result, request);

        return response;
    }

    /**
     * <p>
     * doPut.
     * </p>
     *
     * @param request
     *            a {@link Request}
     *            object.
     * @param <T>
     *            a T object.
     * @return a {@link Response}
     *         object.
     */
    protected <T extends Response<?>> T doPut(Request<T> request) {
        String payload = RequestUtil.getRequestPayload(request);
        if(log.isDebugEnabled()) {
            log.debug(payload);
        }

        URI uri = buidURI(request);

        Map<String, String> headers = RequestUtil.resolveHeaders(request);

        Timer timer = Timer.tic();

        Transport transport = getTransportFactory().newTransport();

        TransportResult result = transport.doPut(uri, headers, payload, request);
        timer.toc();

        if (log.isDebugEnabled()) {
            log.debug(result.getStatusCode() + " " + uri.toString() + " took " + timer.getDifference() + "ms");
        }

        T response = toResponse(timer, result, request);

        return response;
    }

    /**
     * <p>
     * doDelete.
     * </p>
     *
     * @param request
     *            a {@link Request}
     *            object.
     * @param <T>
     *            a T object.
     * @return a {@link Response}
     *         object.
     */
    protected <T extends Response<?>> T doDelete(Request<T> request) {
        URI uri = buidURI(request);

        Map<String, String> headers = RequestUtil.resolveHeaders(request);

        Timer timer = Timer.tic();

        Transport transport = getTransportFactory().newTransport();

        TransportResult result = transport.doDelete(uri, headers, request);
        timer.toc();

        if (log.isDebugEnabled()) {
            log.debug(result.getStatusCode() + " " + uri.toString() + " took " + timer.getDifference() + "ms");
        }

        T response = toResponse(timer, result, request);

        return response;
    }

    /**
     * <p>
     * buidURI.
     * </p>
     *
     * @param request
     *            a {@link Request}
     *            object.
     * @param <T>
     *            a T object.
     * @return a {@link URI} object.
     * @since 0.1.1
     */
    protected <T extends Response<?>> URI buidURI(Request<T> request) {
        URL url = getURL();

        String path = inquirePath(request);
        Map<String, String> qry = RequestUtil.resolveQueryPart(request);

        Set<String> keys = qry.keySet();
        String query = "";
        if (!keys.isEmpty()) {
            for (String key : keys) {
                String value = qry.get(key);
                if(value != null && !value.equals(Default.NOTSET)) {
                    query += (!query.isEmpty() ? "&" : "?") + key + (value != null && !value.isEmpty() ? "=" + value : "");
                }
            }
        }

        String base = url.toString();
        if (base.endsWith("/") && path.startsWith("/")) {
            if (path.length() == 1) {
                path = "";
            } else {
                path = path.substring(1);
            }
        }

        String fqrn = base + path + query;
        try {
            return new URI(fqrn);
        } catch (URISyntaxException e) {
            throw new TransportException(e);
        }
    }

    private <T extends Response<?>> T toResponse(Timer timer, TransportResult transportResult, Request<T> request) {
        T response = null;
        HttpStatus status = HttpStatus.getStatus(transportResult.getStatusCode());
        response = deserialize(transportResult.getResult(), request);
        response.setStatusCode(status.getCode());
        if (response.getStatusMessage() == null) {
            response.setStatusMessage(status.toString());
        }
        response.setResponseTime(timer.getDifference());
        return response;
    }

    /**
     * <p>
     * inquirePath.
     * </p>
     *
     * @param request
     *            a {@link Request}
     *            object.
     * @param <T>
     *            a T object.
     * @return a {@link String} object.
     */
    protected <T extends Response<?>> String inquirePath(Request<T> request) {
        return PathUtil.resolveResourcePath(request);
    }

    public abstract URL getURL();

    /**
     * <p>
     * deserialize.
     * </p>
     *
     * @param response
     *            a {@link String} object.
     * @param request
     *            a {@link Request}
     *            object.
     * @param <T>
     *            a T object.
     * @return a {@link Response}
     *         object.
     */
    protected abstract <T extends Response<?>> T deserialize(String response, Request<T> request);
}
