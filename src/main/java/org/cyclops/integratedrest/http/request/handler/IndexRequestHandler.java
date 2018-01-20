package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.cyclops.integratedrest.api.http.request.IRequestHandler;
import org.cyclops.integratedrest.json.JsonUtil;

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

        responseObject.addProperty("@id", JsonUtil.absolutizePath("/"));
        responseObject.addProperty("networkElementsPage", JsonUtil.absolutizePath("networkElement"));
        responseObject.addProperty("networksPage", JsonUtil.absolutizePath("network"));
        responseObject.addProperty("partsPage", JsonUtil.absolutizePath("registry/part"));
        responseObject.addProperty("aspectsPage", JsonUtil.absolutizePath("registry/aspect"));
        responseObject.addProperty("valueTypesPage", JsonUtil.absolutizePath("registry/value"));
        responseObject.addProperty("itemsPage", JsonUtil.absolutizePath("registry/item"));
        responseObject.addProperty("blocksPage", JsonUtil.absolutizePath("registry/block"));
        responseObject.addProperty("fluidsPage", JsonUtil.absolutizePath("registry/fluid"));
        responseObject.addProperty("modsPage", JsonUtil.absolutizePath("registry/mod"));

        return HttpResponseStatus.OK;
    }
}
