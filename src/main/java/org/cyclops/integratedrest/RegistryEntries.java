package org.cyclops.integratedrest;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;
import org.cyclops.integratedrest.inventory.container.ContainerHttp;
import org.cyclops.integratedrest.tileentity.TileHttp;

/**
 * Referenced registry entries.
 * @author rubensworks
 */
public class RegistryEntries {

    @ObjectHolder("integratedrest:http")
    public static final Item ITEM_BLOCK_HTTP = null;

    @ObjectHolder("integratedrest:http")
    public static final Block BLOCK_HTTP = null;

    @ObjectHolder("integratedrest:http")
    public static final TileEntityType<TileHttp> TILE_ENTITY_HTTP = null;

    @ObjectHolder("integratedrest:http")
    public static final ContainerType<ContainerHttp> CONTAINER_HTTP = null;

}
