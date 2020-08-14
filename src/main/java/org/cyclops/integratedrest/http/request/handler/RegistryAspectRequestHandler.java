package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.api.part.aspect.IAspectRegistry;
import org.cyclops.integratedrest.api.http.request.IRequestHandler;
import org.cyclops.integratedrest.json.JsonUtil;

/**
 * Request handler for registry/aspect requests.
 * @author rubensworks
 */
public class RegistryAspectRequestHandler implements IRequestHandler {

    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        IAspectRegistry registry = IntegratedDynamics._instance.getRegistryManager().getRegistry(IAspectRegistry.class);
        if (path.length == 0) {
            JsonArray array = new JsonArray();
            for (IAspect element : registry.getAspects()) {
                JsonObject object = new JsonObject();
                JsonUtil.addAspectTypeInfo(object, element);
                array.add(object);
            }
            responseObject.addProperty("@id", JsonUtil.absolutizePath("/"));
            responseObject.add("aspects", array);
            return HttpResponseStatus.OK;
        } else {
            IAspect element = registry.getAspect(RegistryNamespacedRequestHandler.pathToResourceLocation(path));
            if (element != null) {
                JsonUtil.addAspectTypeInfo(responseObject, element);
                return HttpResponseStatus.OK;
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
