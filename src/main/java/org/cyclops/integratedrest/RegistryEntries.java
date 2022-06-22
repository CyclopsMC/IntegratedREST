package org.cyclops.integratedrest;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;
import org.cyclops.integratedrest.inventory.container.ContainerHttp;
import org.cyclops.integratedrest.blockentity.BlockEntityHttp;

/**
 * Referenced registry entries.
 * @author rubensworks
 */
public class RegistryEntries {

    @ObjectHolder(registryName = "item", value = "integratedrest:http")
    public static final Item ITEM_BLOCK_HTTP = null;

    @ObjectHolder(registryName = "block", value = "integratedrest:http")
    public static final Block BLOCK_HTTP = null;

    @ObjectHolder(registryName = "block_entity_type", value = "integratedrest:http")
    public static final BlockEntityType<BlockEntityHttp> BLOCK_ENTITY_HTTP = null;

    @ObjectHolder(registryName = "menu", value = "integratedrest:http")
    public static final MenuType<ContainerHttp> CONTAINER_HTTP = null;

}
