package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import org.cyclops.integratedrest.json.JsonUtil;

/**
 * Request handler for registry/block requests.
 * @author rubensworks
 */
public class RegistryBlockRequestHandler extends RegistryNamespacedRequestHandler<Block> {

    @Override
    protected Registry<Block> getRegistry() {
        return BuiltInRegistries.BLOCK;
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
