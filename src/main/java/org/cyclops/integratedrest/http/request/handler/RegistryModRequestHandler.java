package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import org.cyclops.integratedrest.api.http.request.IRequestHandler;
import org.cyclops.integratedrest.json.JsonUtil;

/**
 * Request handler for registry/mod requests.
 * @author rubensworks
 */
public class RegistryModRequestHandler implements IRequestHandler {

    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        if (path.length == 0) {
            JsonArray array = new JsonArray();
            for (IModInfo element : ModList.get().getMods()) {
                JsonObject object = new JsonObject();
                JsonUtil.addModInfo(object, element);
                array.add(object);
            }
            responseObject.addProperty("@id", JsonUtil.absolutizePath("/"));
            responseObject.add("mods", array);
            return HttpResponseStatus.OK;
        } else {
            String modId = String.join("/", path);
            ModContainer element = ModList.get().getModContainerById(modId).orElse(null);
            if (element != null) {
                JsonUtil.addModInfo(responseObject, element.getModInfo());
                return HttpResponseStatus.OK;
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
