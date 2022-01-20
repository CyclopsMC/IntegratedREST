package org.cyclops.integratedrest;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.cyclops.integrateddynamics.api.evaluate.IValueInterface;

/**
 * Used capabilities for this mod.
 * @author rubensworks
 */
public class Capabilities {
    public static Capability<IValueInterface> VALUE_INTERFACE = CapabilityManager.get(new CapabilityToken<>(){});
}
