package org.cyclops.integratedrest.evaluate;

import net.minecraft.nbt.NBTTagCompound;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.integrateddynamics.api.item.IVariableFacadeHandler;
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
    public String getTypeId() {
        return "http";
    }

    @Override
    public IHttpVariableFacade getVariableFacade(int id, NBTTagCompound tag) {
        if(!tag.hasKey("partId", MinecraftHelpers.NBTTag_Types.NBTTagInt.ordinal())) {
            return INVALID_FACADE;
        }
        return new HttpVariableFacade(id, tag.getInteger("partId"));
    }

    @Override
    public void setVariableFacade(NBTTagCompound tag, IHttpVariableFacade variableFacade) {
        tag.setInteger("partId", variableFacade.getProxyId());
    }
}
