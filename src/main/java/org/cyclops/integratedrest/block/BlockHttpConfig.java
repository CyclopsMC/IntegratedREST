package org.cyclops.integratedrest.block;

import net.minecraft.world.level.block.SoundType;
import org.cyclops.cyclopscore.config.extendedconfig.BlockConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.integrateddynamics.item.ItemBlockProxy;
import org.cyclops.integratedrest.IntegratedRest;

/**
 * Config for {@link BlockHttp}.
 * @author rubensworks
 */
public class BlockHttpConfig extends BlockConfigCommon<IModBase> {

    public BlockHttpConfig() {
        super(
            IntegratedRest._instance,
                "http",
                (eConfig, properties) -> new BlockHttp(properties
                        .strength(5.0F)
                        .sound(SoundType.METAL)),
                (eConfig, block) -> new ItemBlockProxy(block, eConfig.createDefaultItemProperties())
        );
    }

}
