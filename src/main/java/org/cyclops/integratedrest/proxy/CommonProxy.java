package org.cyclops.integratedrest.proxy;

import org.cyclops.cyclopscore.init.ModBaseNeoForge;
import org.cyclops.cyclopscore.proxy.CommonProxyComponent;
import org.cyclops.integratedrest.IntegratedRest;

/**
 * Proxy for server and client side.
 * @author rubensworks
 *
 */
public class CommonProxy extends CommonProxyComponent {

    @Override
    public ModBaseNeoForge<IntegratedRest> getMod() {
        return IntegratedRest._instance;
    }

}
