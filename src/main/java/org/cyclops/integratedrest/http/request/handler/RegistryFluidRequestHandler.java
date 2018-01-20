package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.cyclops.integratedrest.api.http.request.IRequestHandler;
import org.cyclops.integratedrest.json.JsonUtil;

import java.util.Map;

/**
 * Request handler for registry/fluid requests.
 * @author rubensworks
 */
public class RegistryFluidRequestHandler implements IRequestHandler {

    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        Map<String, Fluid> registry = FluidRegistry.getRegisteredFluids();
        if (path.length == 0) {
            JsonArray array = new JsonArray();
            for (Fluid element : registry.values()) {
                JsonObject object = new JsonObject();
                JsonUtil.addFluidInfo(object, element);
                array.add(object);
            }
            responseObject.addProperty("@id", JsonUtil.absolutizePath("/"));
            responseObject.add("fluids", array);
            return HttpResponseStatus.OK;
        } else {
            String name = String.join(".", path);
            Fluid element = registry.get(name);
            if (element != null) {
                JsonUtil.addFluidInfo(responseObject, element);
                return HttpResponseStatus.OK;
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
