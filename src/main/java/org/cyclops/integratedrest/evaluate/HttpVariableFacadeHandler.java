package org.cyclops.integratedrest.evaluate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.cyclops.integrateddynamics.api.evaluate.variable.IVariable;
import org.cyclops.integrateddynamics.api.evaluate.variable.ValueDeseralizationContext;
import org.cyclops.integrateddynamics.api.item.IVariableFacade;
import org.cyclops.integrateddynamics.api.item.IVariableFacadeHandler;
import org.cyclops.integratedrest.Reference;
import org.cyclops.integratedrest.api.item.IHttpVariableFacade;
import org.cyclops.integratedrest.item.HttpVariableFacade;

/**
 * Handler for http variable facades.
 * @author rubensworks
 */
public class HttpVariableFacadeHandler implements IVariableFacadeHandler<IHttpVariableFacade> {

    private static final IHttpVariableFacade INVALID_FACADE = new HttpVariableFacade(false, -1);
    private static HttpVariableFacadeHandler _instance;

    private HttpVariableFacadeHandler() {

    }

    public static HttpVariableFacadeHandler getInstance() {
        if(_instance == null) _instance = new HttpVariableFacadeHandler();
        return _instance;
    }

    @Override
    public ResourceLocation getUniqueName() {
        return new ResourceLocation(Reference.MOD_ID, "http");
    }

    @Override
    public IHttpVariableFacade getVariableFacade(ValueDeseralizationContext valueDeseralizationContext, int id, CompoundTag tag) {
        if(!tag.contains("partId", Tag.TAG_INT)) {
            return INVALID_FACADE;
        }
        return new HttpVariableFacade(id, tag.getInt("partId"));
    }

    @Override
    public void setVariableFacade(CompoundTag tag, IHttpVariableFacade variableFacade) {
        tag.putInt("partId", variableFacade.getProxyId());
    }

    @Override
    public boolean isInstance(IVariableFacade variableFacade) {
        return variableFacade instanceof HttpVariableFacade;
    }

    @Override
    public boolean isInstance(IVariable<?> variable) {
        return variable instanceof IVariable;
    }
}
