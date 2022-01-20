package org.cyclops.integratedrest.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.Item;
import org.cyclops.cyclopscore.config.extendedconfig.BlockConfig;
import org.cyclops.integrateddynamics.item.ItemBlockProxy;
import org.cyclops.integratedrest.IntegratedRest;

/**
 * Config for {@link BlockHttp}.
 * @author rubensworks
 */
public class BlockHttpConfig extends BlockConfig {

    public BlockHttpConfig() {
        super(
            IntegratedRest._instance,
                "http",
                eConfig -> new BlockHttp(Block.Properties.of(Material.HEAVY_METAL)
                        .strength(5.0F)
                        .sound(SoundType.METAL)),
                (eConfig, block) -> new ItemBlockProxy(block, new Item.Properties()
                        .tab(IntegratedRest._instance.getDefaultItemGroup()))
        );
    }

}
