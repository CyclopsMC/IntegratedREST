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
 * Request handler for registry/parts requests.
 * @author rubensworks
 */
public class RegistryPartsRequestHandler implements IRequestHandler {

    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        IPartTypeRegistry partTypeRegistry = IntegratedDynamics._instance.getRegistryManager().getRegistry(IPartTypeRegistry.class);
        if (path.length == 0) {
            JsonArray jsonParts = new JsonArray();
            for (IPartType partType : partTypeRegistry.getPartTypes()) {
                JsonObject partTypeObject = new JsonObject();
                JsonUtil.addPartTypeInfo(partTypeObject, partType);
                jsonParts.add(partTypeObject);
            }
            responseObject.add("parts", jsonParts);
            return HttpResponseStatus.OK;
        } else {
            String partName = String.join("/", path);
            IPartType partType = partTypeRegistry.getPartType(partName);
            if (partType != null) {
                JsonUtil.addPartTypeInfo(responseObject, partType);
                return HttpResponseStatus.OK;
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
