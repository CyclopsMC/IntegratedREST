package org.cyclops.integratedrest.item;

import net.minecraft.client.renderer.item.ItemModel;
import org.cyclops.integrateddynamics.api.client.model.IVariableModelBaked;
import org.cyclops.integrateddynamics.core.item.ProxyVariableFacadeClient;
import org.cyclops.integratedrest.client.model.HttpVariableModelProviders;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class HttpVariableFacadeClient extends ProxyVariableFacadeClient {

    private final HttpVariableFacade variableFacade;

    public HttpVariableFacadeClient(HttpVariableFacade variableFacade) {
        super(variableFacade);
        this.variableFacade = variableFacade;
    }

    @Nullable
    @Override
    public ItemModel getItemModelOverlay(IVariableModelBaked variableModelBaked) {
        if (this.variableFacade.isValid()) {
            return variableModelBaked.getSubModels(HttpVariableModelProviders.HTTP).getBakedModel();
        }
        return null;
    }
}
