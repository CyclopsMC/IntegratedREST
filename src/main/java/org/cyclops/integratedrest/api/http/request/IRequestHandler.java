package org.cyclops.integratedrest.api.http.request;

import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Handles an HTTP request for a certain path.
 * @author rubensworks
 */
public interface IRequestHandler {

    /**
     * Handle the given request.
     * @param path The remainder of the path array, excluding the path entry identifying this handler.
     * @param request The request object.
     * @param responseObject The response JSON object that can be written to.
     * @return A response status.
     */
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject);

}
