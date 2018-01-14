package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import org.cyclops.integratedrest.api.http.request.IRequestHandler;
import org.cyclops.integratedrest.json.JsonUtil;

/**
 * Abstract request handler for namespaced registry calls.
 * @author rubensworks
 */
public abstract class RegistryNamespacedRequestHandler<T> implements IRequestHandler {

    protected abstract RegistryNamespaced<ResourceLocation, T> getRegistry();

    protected abstract void handleElement(T element, JsonObject jsonObject);

    protected abstract String getElementsName();

    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        RegistryNamespaced<ResourceLocation, T> registry = getRegistry();
        if (path.length == 0) {
            JsonArray array = new JsonArray();
            for (T element : registry) {
                JsonObject object = new JsonObject();
                handleElement(element, object);
                array.add(object);
            }
            responseObject.add(getElementsName(), array);
            return HttpResponseStatus.OK;
        } else {
            ResourceLocation resourceLocation = new ResourceLocation(String.join("/", path).replaceFirst("\\/", ":"));
            T element = registry.getObject(resourceLocation);
            if (element != null) {
                handleElement(element, responseObject);
                return HttpResponseStatus.OK;
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
