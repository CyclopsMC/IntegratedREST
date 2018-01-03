package org.cyclops.integratedrest;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import org.cyclops.integrateddynamics.api.evaluate.IValueInterface;

/**
 * Used capabilities for this mod.
 * @author rubensworks
 */
public class Capabilities {
    @CapabilityInject(IValueInterface.class)
    public static Capability<IValueInterface> VALUE_INTERFACE = null;
}
