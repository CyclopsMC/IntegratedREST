package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
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
            for (ModContainer element : Loader.instance().getActiveModList()) {
                JsonObject object = new JsonObject();
                JsonUtil.addModInfo(object, element);
                array.add(object);
            }
            responseObject.add("mods", array);
            return HttpResponseStatus.OK;
        } else {
            String modId = String.join("/", path);
            ModContainer element = Loader.instance().getIndexedModList().get(modId);
            if (element != null) {
                JsonUtil.addModInfo(responseObject, element);
                return HttpResponseStatus.OK;
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
