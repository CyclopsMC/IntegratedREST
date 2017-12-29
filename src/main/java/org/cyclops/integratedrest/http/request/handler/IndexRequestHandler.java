package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.cyclops.integratedrest.api.http.request.IRequestHandler;

/**
 * The default request handler for the index page.
 * @author rubensworks
 */
public class IndexRequestHandler implements IRequestHandler {
    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        if (path.length > 0) {
            return HttpResponseStatus.NOT_FOUND;
        }

        responseObject.addProperty("parts", "TODO"); // TODO
        responseObject.addProperty("networks", "TODO"); // TODO

        return HttpResponseStatus.OK;
    }
}
