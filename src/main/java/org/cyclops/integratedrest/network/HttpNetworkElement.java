package org.cyclops.integratedrest.network;

import net.minecraft.util.ResourceLocation;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.Reference;
import org.cyclops.integrateddynamics.network.ProxyNetworkElement;

/**
 * Network element for http proxies.
 * @author rubensworks
 */
public class HttpNetworkElement extends ProxyNetworkElement {

    public static final ResourceLocation GROUP = new ResourceLocation(Reference.MOD_ID, "http");

    public HttpNetworkElement(DimPos pos) {
        super(pos);
    }

    @Override
    public ResourceLocation getGroup() {
        return HttpNetworkElement.GROUP;
    }

}
