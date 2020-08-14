package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.cyclops.integratedrest.json.JsonUtil;

/**
 * Request handler for registry/item requests.
 * @author rubensworks
 */
public class RegistryItemRequestHandler extends RegistryNamespacedRequestHandler<Item> {

    @Override
    protected IForgeRegistry<Item> getRegistry() {
        return ForgeRegistries.ITEMS;
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
