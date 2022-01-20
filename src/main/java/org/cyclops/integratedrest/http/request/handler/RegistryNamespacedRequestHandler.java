package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.cyclops.integratedrest.api.http.request.IRequestHandler;
import org.cyclops.integratedrest.json.JsonUtil;

/**
 * Abstract request handler for namespaced registry calls.
 * @author rubensworks
 */
public abstract class RegistryNamespacedRequestHandler<T extends IForgeRegistryEntry<T>> implements IRequestHandler {

    protected abstract IForgeRegistry<T> getRegistry();

    protected abstract void handleElement(T element, JsonObject jsonObject);

    protected abstract String getElementsName();

    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        IForgeRegistry<T> registry = getRegistry();
        if (path.length == 0) {
            JsonArray array = new JsonArray();
            for (T element : registry) {
                JsonObject object = new JsonObject();
                handleElement(element, object);
                array.add(object);
            }
            responseObject.addProperty("@id", JsonUtil.absolutizePath("/"));
            responseObject.add(getElementsName(), array);
            return HttpResponseStatus.OK;
        } else {
            ResourceLocation resourceLocation = pathToResourceLocation(path);
            T element = registry.getValue(resourceLocation);
            if (element != null) {
                handleElement(element, responseObject);
                return HttpResponseStatus.OK;
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }

    public static ResourceLocation pathToResourceLocation(String[] path) {
        return new ResourceLocation(String.join("/", path).replaceFirst("\\/", ":"));
    }
}
