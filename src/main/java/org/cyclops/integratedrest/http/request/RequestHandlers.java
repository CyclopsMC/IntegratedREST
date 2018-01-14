package org.cyclops.integratedrest.http.request;

import org.cyclops.integratedrest.IntegratedRest;
import org.cyclops.integratedrest.api.http.request.IRequestHandlerRegistry;
import org.cyclops.integratedrest.http.request.handler.*;

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
        REGISTRY.registerHandler("registry/part", new RegistryPartRequestHandler());
        REGISTRY.registerHandler("registry/aspect", new RegistryAspectRequestHandler());
        REGISTRY.registerHandler("registry/value", new RegistryValueRequestHandler());
        REGISTRY.registerHandler("registry/item", new RegistryItemRequestHandler());
        REGISTRY.registerHandler("registry/block", new RegistryBlockRequestHandler());
        REGISTRY.registerHandler("registry/mod", new RegistryModRequestHandler());
    }

}
