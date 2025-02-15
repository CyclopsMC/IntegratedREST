package org.cyclops.integratedrest.blockentity;

import com.google.common.collect.Sets;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.cyclops.cyclopscore.config.extendedconfig.BlockEntityConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.integratedrest.IntegratedRest;
import org.cyclops.integratedrest.RegistryEntries;

/**
 * Config for the {@link BlockEntityHttp}.
 * @author rubensworks
 *
 */
public class BlockEntityHttpConfig extends BlockEntityConfigCommon<BlockEntityHttp, IModBase> {

    public BlockEntityHttpConfig() {
        super(
                IntegratedRest._instance,
                "http",
                (eConfig) -> new BlockEntityType<>(BlockEntityHttp::new,
                        Sets.newHashSet(RegistryEntries.BLOCK_HTTP.get()))
        );
        IntegratedRest._instance.getModEventBus().addListener(new BlockEntityHttp.CapabilityRegistrar(this::getInstance)::register);
    }

}
