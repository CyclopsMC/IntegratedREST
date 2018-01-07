package org.cyclops.integratedrest.block;

import net.minecraft.item.ItemBlock;
import org.cyclops.cyclopscore.config.extendedconfig.BlockContainerConfig;
import org.cyclops.integratedrest.IntegratedRest;
import org.cyclops.integratedrest.item.ItemBlockHttp;

/**
 * Config for {@link BlockHttp}.
 * @author rubensworks
 */
public class BlockHttpConfig extends BlockContainerConfig {

    /**
     * The unique instance.
     */
    public static BlockHttpConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockHttpConfig() {
        super(
            IntegratedRest._instance,
            true,
            "http",
            null,
            BlockHttp.class
        );
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockHttp.class;
    }
}
