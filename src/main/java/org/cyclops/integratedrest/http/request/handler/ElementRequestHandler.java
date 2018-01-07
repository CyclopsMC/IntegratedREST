package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.INetworkElement;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetworkElement;
import org.cyclops.integrateddynamics.api.network.IPositionedNetworkElement;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueHelpers;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.persist.world.NetworkWorldStorage;
import org.cyclops.integratedrest.api.http.request.IRequestHandler;
import org.cyclops.integratedrest.json.JsonUtil;
import org.cyclops.integratedrest.tileentity.TileHttp;

import java.util.Optional;

/**
 * Request handler for /part requests.
 * @author rubensworks
 */
public class ElementRequestHandler implements IRequestHandler {
    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        NetworkWorldStorage worldStorage = NetworkWorldStorage.getInstance(IntegratedDynamics._instance);
        if (path.length == 0) {
            if (!request.method().equals(HttpMethod.GET)) {
                return HttpResponseStatus.BAD_REQUEST;
            }

            // All parts
            JsonArray jsonParts = new JsonArray();
            for (INetwork network : worldStorage.getNetworks()) {
                for (INetworkElement element : network.getElements()) {
                    JsonObject jsonElement = new JsonObject();
                    JsonUtil.addNetworkElementInfo(jsonElement, element, network);
                    jsonParts.add(jsonElement);
                }
            }
            responseObject.add("parts", jsonParts);
            return HttpResponseStatus.OK;
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
