package org.cyclops.integratedrest.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.integrateddynamics.api.client.model.IVariableModelBaked;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.core.item.ProxyVariableFacade;
import org.cyclops.integratedrest.api.item.IHttpVariableFacade;
import org.cyclops.integratedrest.client.model.HttpVariableModelProviders;

import java.util.List;
import java.util.Random;

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

    @Override
    protected IFormattableTextComponent getProxyNotInNetworkError() {
        return new TranslationTextComponent("http.integratedrest.error.http_not_in_network", Integer.toString(getProxyId()));
    }

    @Override
    protected IFormattableTextComponent getProxyInvalidError() {
        return new TranslationTextComponent("http.integratedrest.error.http_invalid", Integer.toString(getProxyId()));
    }

    @Override
    protected IFormattableTextComponent getProxyInvalidTypeError(IPartNetwork network,
                                                      IValueType containingValueType,
                                                      IValueType actualType) {
        return new TranslationTextComponent("http.integratedrest.error.http_invalid_type",
                new TranslationTextComponent(containingValueType.getTranslationKey()),
                new TranslationTextComponent(actualType.getTranslationKey()));
    }

    protected ITextComponent getProxyTooltip() {
        return new TranslationTextComponent("http.integratedrest.tooltip.delay_id", getProxyId());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addModelOverlay(IVariableModelBaked variableModelBaked, List<BakedQuad> quads, Random rand, IModelData modelData) {
        if(isValid()) {
            quads.addAll(variableModelBaked.getSubModels(HttpVariableModelProviders.HTTP).getBakedModel().getQuads(null, null, rand));
        }
    }
}
