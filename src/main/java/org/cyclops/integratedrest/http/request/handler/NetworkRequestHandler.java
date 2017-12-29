package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.INetworkElement;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetworkElement;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.persist.world.NetworkWorldStorage;
import org.cyclops.integratedrest.json.JsonUtil;
import org.cyclops.integratedrest.api.http.request.IRequestHandler;

/**
 * Request handler for /network requests.
 * @author rubensworks
 */
public class NetworkRequestHandler implements IRequestHandler {
    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
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
                IPartNetwork partNetwork = NetworkHelpers.getPartNetwork(network);
                for (INetworkElement networkElement : network.getElements()) {
                    JsonObject jsonPart = new JsonObject();
                    JsonUtil.addNetworkElementInfo(jsonPart, networkElement);
                    if (partNetwork != null && networkElement instanceof IPartNetworkElement) {
                        JsonUtil.addPartNetworkElementInfo(jsonPart, (IPartNetworkElement<?, ?>) networkElement, partNetwork);
                    }
                    jsonElements.add(jsonPart);
                }
                responseObject.add("elements", jsonElements);
                return HttpResponseStatus.OK;
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
