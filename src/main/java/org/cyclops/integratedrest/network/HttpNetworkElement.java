package org.cyclops.integratedrest.network;

import net.minecraft.resources.ResourceLocation;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.network.ProxyNetworkElement;
import org.cyclops.integratedrest.Reference;

/**
 * Network element for http proxies.
 * @author rubensworks
 */
public class HttpNetworkElement extends ProxyNetworkElement {

    public static final ResourceLocation GROUP = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "http");

    public HttpNetworkElement(DimPos pos) {
        super(pos);
    }

    @Override
    public ResourceLocation getGroup() {
        return HttpNetworkElement.GROUP;
    }

}
