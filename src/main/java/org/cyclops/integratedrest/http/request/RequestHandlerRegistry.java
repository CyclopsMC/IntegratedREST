package org.cyclops.integratedrest.http.request;

import com.google.common.collect.Maps;
import org.cyclops.integratedrest.api.http.request.IRequestHandler;
import org.cyclops.integratedrest.api.http.request.IRequestHandlerRegistry;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Implementation for {@link IRequestHandlerRegistry}.
 * @author rubensworks
 */
public class RequestHandlerRegistry implements IRequestHandlerRegistry {

    private static RequestHandlerRegistry INSTANCE = new RequestHandlerRegistry();

    private RequestHandlerRegistry() {

    }

    public static RequestHandlerRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, IRequestHandler> handlers = Maps.newHashMap();

    @Override
    public void registerHandler(String path, IRequestHandler handler) {
        handlers.put(path, handler);
    }

    @Override
    @Nullable
    public IRequestHandler getHandler(String path) {
        return handlers.get(path);
    }
}
