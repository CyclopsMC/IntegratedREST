package org.cyclops.integratedrest.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.integrateddynamics.api.client.model.IVariableModelBaked;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.core.item.ProxyVariableFacade;
import org.cyclops.integratedrest.api.item.IHttpVariableFacade;
import org.cyclops.integratedrest.client.model.HttpVariableModelProviders;

import java.util.List;

/**
 * Variable facade for variables determined by http blocks.
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HttpVariableFacade extends ProxyVariableFacade implements IHttpVariableFacade {

    public HttpVariableFacade(boolean generateId, int proxyId) {
        super(generateId, proxyId);
    }

    public HttpVariableFacade(int id, int proxyId) {
        super(id, proxyId);
    }

    protected L10NHelpers.UnlocalizedString getProxyNotInNetworkError() {
        return new L10NHelpers.UnlocalizedString("http.integratedrest.error.http_not_in_network", Integer.toString(getProxyId()));
    }

    protected L10NHelpers.UnlocalizedString getProxyInvalidError() {
        return new L10NHelpers.UnlocalizedString("http.integratedrest.error.http_invalid", Integer.toString(getProxyId()));
    }

    protected L10NHelpers.UnlocalizedString getProxyInvalidTypeError(IPartNetwork network,
                                                                     IValueType containingValueType,
                                                                     IValueType actualType) {
        return new L10NHelpers.UnlocalizedString("http.integratedrest.error.http_invalid_type",
                new L10NHelpers.UnlocalizedString(containingValueType.getUnlocalizedName()),
                new L10NHelpers.UnlocalizedString(actualType.getUnlocalizedName()));
    }

    protected String getProxyTooltip() {
        return L10NHelpers.localize("http.integratedrest.tooltip.delay_id", getProxyId());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addModelOverlay(IVariableModelBaked variableModelBaked, List<BakedQuad> quads) {
        if(isValid()) {
            quads.addAll(variableModelBaked.getSubModels(HttpVariableModelProviders.HTTP).getBakedModel().getQuads(null, null, 0L));
        }
    }
}
