package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.cyclops.integratedrest.json.JsonUtil;

/**
 * Request handler for registry/item requests.
 * @author rubensworks
 */
public class RegistryItemRequestHandler extends RegistryNamespacedRequestHandler<Item> {

    @Override
    protected Registry<Item> getRegistry() {
        return BuiltInRegistries.ITEM;
    }

    @Override
    protected void handleElement(Item element, JsonObject jsonObject) {
        JsonUtil.addItemInfo(jsonObject, element);
    }

    @Override
    protected String getElementsName() {
        return "items";
    }
}
