package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.cyclops.integratedrest.json.JsonUtil;

/**
 * Request handler for registry/block requests.
 * @author rubensworks
 */
public class RegistryBlockRequestHandler extends RegistryNamespacedRequestHandler<Block> {

    @Override
    protected IForgeRegistry<Block> getRegistry() {
        return ForgeRegistries.BLOCKS;
    }

    @Override
    protected void handleElement(Block element, JsonObject jsonObject) {
        JsonUtil.addBlockInfo(jsonObject, element);
    }

    @Override
    protected String getElementsName() {
        return "blocks";
    }
}
