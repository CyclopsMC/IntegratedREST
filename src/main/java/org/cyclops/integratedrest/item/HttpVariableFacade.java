package org.cyclops.integratedrest.item;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
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
public class HttpVariableFacade extends ProxyVariableFacade implements IHttpVariableFacade {

    public HttpVariableFacade(boolean generateId, int proxyId) {
        super(generateId, proxyId);
    }

    public HttpVariableFacade(int id, int proxyId) {
        super(id, proxyId);
    }

    @Override
    protected MutableComponent getProxyNotInNetworkError() {
        return Component.translatable("http.integratedrest.error.http_not_in_network", Integer.toString(getProxyId()));
    }

    @Override
    protected MutableComponent getProxyInvalidError() {
        return Component.translatable("http.integratedrest.error.http_invalid", Integer.toString(getProxyId()));
    }

    @Override
    protected MutableComponent getProxyInvalidTypeError(IPartNetwork network,
                                                      IValueType containingValueType,
                                                      IValueType actualType) {
        return Component.translatable("http.integratedrest.error.http_invalid_type",
                Component.translatable(containingValueType.getTranslationKey()),
                Component.translatable(actualType.getTranslationKey()));
    }

    protected Component getProxyTooltip() {
        return Component.translatable("http.integratedrest.tooltip.delay_id", getProxyId());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addModelOverlay(IVariableModelBaked variableModelBaked, List<BakedQuad> quads, RandomSource rand, ModelData modelData) {
        if(isValid()) {
            quads.addAll(variableModelBaked.getSubModels(HttpVariableModelProviders.HTTP).getBakedModel().getQuads(null, null, rand, modelData, null));
        }
    }
}
