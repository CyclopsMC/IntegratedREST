package org.cyclops.integratedrest.http.request;

import org.cyclops.integratedrest.IntegratedRest;
import org.cyclops.integratedrest.api.http.request.IRequestHandlerRegistry;
import org.cyclops.integratedrest.http.request.handler.ElementHttpRequestHandler;
import org.cyclops.integratedrest.http.request.handler.ElementPartRequestHandler;
import org.cyclops.integratedrest.http.request.handler.ElementRequestHandler;
import org.cyclops.integratedrest.http.request.handler.IndexRequestHandler;
import org.cyclops.integratedrest.http.request.handler.NetworkRequestHandler;
import org.cyclops.integratedrest.http.request.handler.RegistryBlockRequestHandler;
import org.cyclops.integratedrest.http.request.handler.RegistryItemRequestHandler;
import org.cyclops.integratedrest.http.request.handler.RegistryModRequestHandler;
import org.cyclops.integratedrest.http.request.handler.RegistryPartsRequestHandler;

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
        REGISTRY.registerHandler("element/integrateddynamics/part", new ElementPartRequestHandler());
        REGISTRY.registerHandler("element/integrateddynamics/http", new ElementHttpRequestHandler());
        REGISTRY.registerHandler("registry/part", new RegistryPartsRequestHandler());
        // TODO: aspect type
        // TODO: value type
        REGISTRY.registerHandler("registry/item", new RegistryItemRequestHandler());
        REGISTRY.registerHandler("registry/block", new RegistryBlockRequestHandler());
        REGISTRY.registerHandler("registry/mod", new RegistryModRequestHandler());
    }

}
