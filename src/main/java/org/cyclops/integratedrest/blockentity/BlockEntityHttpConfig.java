package org.cyclops.integratedrest.blockentity;

import com.google.common.collect.Sets;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.cyclops.cyclopscore.config.extendedconfig.BlockEntityConfig;
import org.cyclops.integratedrest.IntegratedRest;
import org.cyclops.integratedrest.RegistryEntries;

/**
 * Config for the {@link BlockEntityHttp}.
 * @author rubensworks
 *
 */
public class BlockEntityHttpConfig extends BlockEntityConfig<BlockEntityHttp> {

    public BlockEntityHttpConfig() {
        super(
                IntegratedRest._instance,
                "http",
                (eConfig) -> new BlockEntityType<>(BlockEntityHttp::new,
                        Sets.newHashSet(RegistryEntries.BLOCK_HTTP.get()), null)
        );
        IntegratedRest._instance.getModEventBus().addListener(this::registerCapability);
    }

    protected void registerCapability(RegisterCapabilitiesEvent event) {
        BlockEntityHttp.registerHttpCapabilities(event, getInstance());
    }

}
