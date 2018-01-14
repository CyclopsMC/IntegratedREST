package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.INetworkElement;
import org.cyclops.integrateddynamics.core.persist.world.NetworkWorldStorage;
import org.cyclops.integratedrest.api.http.request.IRequestHandler;

import javax.annotation.Nullable;

/**
 * Request handler for /part requests.
 * @author rubensworks
 */
public abstract class ElementTypeRequestHandler implements IRequestHandler {

    @Nullable
    protected abstract HttpResponseStatus handleElement(int id, INetwork network, INetworkElement networkElement,
                                                        HttpRequest request, JsonObject responseObject);

    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        NetworkWorldStorage worldStorage = NetworkWorldStorage.getInstance(IntegratedDynamics._instance);
        if (path.length == 1) {
            // A single part
            try {
                int id = Integer.parseInt(path[0]);

                for (INetwork network : worldStorage.getNetworks()) {
                    for (INetworkElement element : network.getElements()) {
                        HttpResponseStatus status = handleElement(id, network, element, request, responseObject);
                        if (status != null) {
                            return status;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                return HttpResponseStatus.BAD_REQUEST;
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
