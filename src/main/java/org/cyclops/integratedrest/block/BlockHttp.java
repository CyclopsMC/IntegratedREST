package org.cyclops.integratedrest.block;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import org.cyclops.cyclopscore.block.property.BlockProperty;
import org.cyclops.cyclopscore.config.extendedconfig.BlockConfig;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfig;
import org.cyclops.integrateddynamics.core.block.BlockContainerGuiCabled;
import org.cyclops.integratedrest.client.gui.GuiHttp;
import org.cyclops.integratedrest.inventory.container.ContainerHttp;
import org.cyclops.integratedrest.tileentity.TileHttp;

/**
 * A block that can listen to HTTP PUTs.
 * @author rubensworks
 */
public class BlockHttp extends BlockContainerGuiCabled {

    @BlockProperty
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    private static BlockHttp _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static BlockHttp getInstance() {
        return _instance;
    }

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    public BlockHttp(ExtendedConfig<BlockConfig> eConfig) {
        super(eConfig, TileHttp.class);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerHttp.class;
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiHttp.class;
    }
}
