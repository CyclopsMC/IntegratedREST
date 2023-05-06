package org.cyclops.integratedrest.client.model;

import net.minecraft.resources.ResourceLocation;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.client.model.IVariableModelProviderRegistry;
import org.cyclops.integrateddynamics.core.client.model.SingleVariableModelProvider;
import org.cyclops.integratedrest.Reference;

/**
 * Collection of variable model providers.
 * @author rubensworks
 */
public class HttpVariableModelProviders {

    public static final IVariableModelProviderRegistry REGISTRY = IntegratedDynamics._instance.getRegistryManager().getRegistry(IVariableModelProviderRegistry.class);
    public static final SingleVariableModelProvider HTTP = REGISTRY.addProvider(new SingleVariableModelProvider(new ResourceLocation(Reference.MOD_ID, "customoverlay/http")));

    public static void load() {}

}
