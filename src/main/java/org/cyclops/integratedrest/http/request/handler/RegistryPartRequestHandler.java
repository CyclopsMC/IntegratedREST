package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.IPartTypeRegistry;
import org.cyclops.integratedrest.api.http.request.IRequestHandler;
import org.cyclops.integratedrest.json.JsonUtil;

/**
 * Request handler for registry/part requests.
 * @author rubensworks
 */
public class RegistryPartRequestHandler implements IRequestHandler {

    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        IPartTypeRegistry registry = IntegratedDynamics._instance.getRegistryManager().getRegistry(IPartTypeRegistry.class);
        if (path.length == 0) {
            JsonArray array = new JsonArray();
            for (IPartType element : registry.getPartTypes()) {
                JsonObject object = new JsonObject();
                JsonUtil.addPartTypeInfo(object, element);
                array.add(object);
            }
            responseObject.addProperty("@id", JsonUtil.absolutizePath("/"));
            responseObject.add("parts", array);
            return HttpResponseStatus.OK;
        } else {
            IPartType element = registry.getPartType(RegistryNamespacedRequestHandler.pathToResourceLocation(path));
            if (element != null) {
                JsonUtil.addPartTypeInfo(responseObject, element);
                return HttpResponseStatus.OK;
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
