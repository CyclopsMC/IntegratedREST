package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.INetworkElement;
import org.cyclops.integrateddynamics.core.persist.world.NetworkWorldStorage;
import org.cyclops.integratedrest.api.http.request.IRequestHandler;
import org.cyclops.integratedrest.json.JsonUtil;

/**
 * Request handler for /network requests.
 * @author rubensworks
 */
public class NetworkRequestHandler implements IRequestHandler {
    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        if (!request.method().equals(HttpMethod.GET)) {
            return HttpResponseStatus.BAD_REQUEST;
        }

        NetworkWorldStorage worldStorage = NetworkWorldStorage.getInstance(IntegratedDynamics._instance);
        if (path.length == 0) {
            // All networks
            JsonArray jsonNetworks = new JsonArray();
            for (INetwork network : worldStorage.getNetworks()) {
                JsonObject jsonNetwork = new JsonObject();
                JsonUtil.addNetworkInfo(jsonNetwork, network);
                jsonNetworks.add(jsonNetwork);
            }
            responseObject.add("networks", jsonNetworks);
            return HttpResponseStatus.OK;
        } else if (path.length == 1) {
            // A single network
            String networkHash = path[0];

            INetwork network = null;
            for (INetwork loopNetwork : worldStorage.getNetworks()) {
                if (Integer.toString(loopNetwork.hashCode()).equals(networkHash)) {
                    network = loopNetwork;
                }
            }
            if (network != null) {
                JsonUtil.addNetworkInfo(responseObject, network);
                JsonArray jsonElements = new JsonArray();
                for (INetworkElement networkElement : network.getElements()) {
                    JsonObject jsonElement = new JsonObject();
                    JsonUtil.addNetworkElementInfo(jsonElement, networkElement, network);
                    jsonElements.add(jsonElement);
                }
                responseObject.add("elements", jsonElements);
                return HttpResponseStatus.OK;
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
