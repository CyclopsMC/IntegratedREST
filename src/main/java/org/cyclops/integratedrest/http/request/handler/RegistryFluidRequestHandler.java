package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import org.cyclops.integratedrest.json.JsonUtil;

/**
 * Request handler for registry/fluid requests.
 * @author rubensworks
 */
public class RegistryFluidRequestHandler extends RegistryNamespacedRequestHandler<Fluid> {

    @Override
    protected Registry<Fluid> getRegistry() {
        return BuiltInRegistries.FLUID;
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
