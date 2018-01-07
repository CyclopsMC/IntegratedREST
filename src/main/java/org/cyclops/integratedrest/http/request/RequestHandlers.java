package org.cyclops.integratedrest.http.request;

import org.cyclops.integratedrest.IntegratedRest;
import org.cyclops.integratedrest.api.http.request.IRequestHandlerRegistry;
import org.cyclops.integratedrest.http.request.handler.ElementHttpRequestHandler;
import org.cyclops.integratedrest.http.request.handler.ElementPartRequestHandler;
import org.cyclops.integratedrest.http.request.handler.IndexRequestHandler;
import org.cyclops.integratedrest.http.request.handler.NetworkRequestHandler;
import org.cyclops.integratedrest.http.request.handler.ElementRequestHandler;

/**
 * Registration code for request handlers.
 * @author rubensworks
 */
public class RequestHandlers {

    public static IRequestHandlerRegistry REGISTRY = IntegratedRest._instance.getRegistryManager().getRegistry(IRequestHandlerRegistry.class);

    public static void load() {
        REGISTRY.registerHandler("", new IndexRequestHandler());
        REGISTRY.registerHandler("network", new NetworkRequestHandler());
        REGISTRY.registerHandler("element", new ElementRequestHandler());
        REGISTRY.registerHandler("element/part", new ElementPartRequestHandler());
        REGISTRY.registerHandler("element/http", new ElementHttpRequestHandler());
        // TODO: part type
        // TODO: aspect type
        // TODO: value type
        // TODO: other registry entries?
    }

}
