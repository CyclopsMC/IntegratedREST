package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonObject;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.cyclops.integratedrest.json.JsonUtil;

/**
 * Request handler for registry/fluid requests.
 * @author rubensworks
 */
public class RegistryFluidRequestHandler extends RegistryNamespacedRequestHandler<Fluid> {

    @Override
    protected IForgeRegistry<Fluid> getRegistry() {
        return ForgeRegistries.FLUIDS;
    }

    @Override
    protected void handleElement(Fluid element, JsonObject jsonObject) {
        JsonUtil.addFluidInfo(jsonObject, element);
    }

    @Override
    protected String getElementsName() {
        return "fluids";
    }
}
