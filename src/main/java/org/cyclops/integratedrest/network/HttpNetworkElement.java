package org.cyclops.integratedrest.network;

import net.minecraft.resources.Identifier;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.network.ProxyNetworkElement;
import org.cyclops.integratedrest.Reference;

/**
 * Network element for http proxies.
 * @author rubensworks
 */
public class HttpNetworkElement extends ProxyNetworkElement {

    public static final Identifier GROUP = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "http");

    public HttpNetworkElement(DimPos pos) {
        super(pos);
    }

    @Override
    public Identifier getGroup() {
        return HttpNetworkElement.GROUP;
    }

}
