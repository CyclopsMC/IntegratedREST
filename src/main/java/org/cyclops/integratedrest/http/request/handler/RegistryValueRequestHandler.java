package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeRegistry;
import org.cyclops.integratedrest.api.http.request.IRequestHandler;
import org.cyclops.integratedrest.json.JsonUtil;

/**
 * Request handler for registry/value requests.
 * @author rubensworks
 */
public class RegistryValueRequestHandler implements IRequestHandler {

    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        IValueTypeRegistry registry = IntegratedDynamics._instance.getRegistryManager().getRegistry(IValueTypeRegistry.class);
        if (path.length == 0) {
            JsonArray array = new JsonArray();
            for (IValueType element : registry.getValueTypes()) {
                JsonObject object = new JsonObject();
                JsonUtil.addValueTypeInfo(object, element);
                array.add(object);
            }
            responseObject.add("values", array);
            return HttpResponseStatus.OK;
        } else {
            String name = String.join(".", path);
            IValueType element = registry.getValueType(name);
            if (element != null) {
                JsonUtil.addValueTypeInfo(responseObject, element);
                return HttpResponseStatus.OK;
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
