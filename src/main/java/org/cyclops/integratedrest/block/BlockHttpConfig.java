package org.cyclops.integratedrest.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
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
                eConfig -> new BlockHttp(Block.Properties.create(Material.ANVIL)
                        .hardnessAndResistance(5.0F)
                        .sound(SoundType.METAL)),
                (eConfig, block) -> new ItemBlockProxy(block, new Item.Properties()
                        .group(IntegratedRest._instance.getDefaultItemGroup()))
        );
    }

}
