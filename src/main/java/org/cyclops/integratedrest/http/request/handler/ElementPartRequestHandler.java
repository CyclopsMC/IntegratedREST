package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.INetworkElement;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetworkElement;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integratedrest.json.JsonUtil;

import javax.annotation.Nullable;

/**
 * Request handler for /element/part requests.
 * @author rubensworks
 */
public class ElementPartRequestHandler extends ElementTypeRequestHandler {
    @Nullable
    @Override
    protected HttpResponseStatus handleElement(int id, INetwork network, INetworkElement networkElement,
                                               HttpRequest request, JsonObject responseObject) {
        if (networkElement instanceof IPartNetworkElement) {
            IPartNetworkElement partNetworkElement = (IPartNetworkElement) networkElement;
            IPartNetwork partNetwork = NetworkHelpers.getPartNetwork(network);
            if (partNetwork != null) {
                if (partNetworkElement.getPartState().getId() == id) {
                    if (request.method().equals(HttpMethod.GET)) {
                        JsonUtil.addNetworkElementInfo(responseObject, networkElement, network);
                        return HttpResponseStatus.OK;
                    } else {
                        return HttpResponseStatus.BAD_REQUEST;
                    }
                }
            } else {
                return HttpResponseStatus.INTERNAL_SERVER_ERROR;
            }
        }
        return null;
    }
}
