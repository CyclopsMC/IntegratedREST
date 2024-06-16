package org.cyclops.integratedrest;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.cyclops.integratedrest.blockentity.BlockEntityHttp;
import org.cyclops.integratedrest.inventory.container.ContainerHttp;

/**
 * Referenced registry entries.
 * @author rubensworks
 */
public class RegistryEntries {

    public static final DeferredHolder<Item, Item> ITEM_BLOCK_HTTP = DeferredHolder.create(Registries.ITEM, new ResourceLocation("integratedrest:http"));

    public static final DeferredHolder<Block, Block> BLOCK_HTTP = DeferredHolder.create(Registries.BLOCK, new ResourceLocation("integratedrest:http"));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityHttp>> BLOCK_ENTITY_HTTP = DeferredHolder.create(Registries.BLOCK_ENTITY_TYPE, new ResourceLocation("integratedrest:http"));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerHttp>> CONTAINER_HTTP = DeferredHolder.create(Registries.MENU, new ResourceLocation("integratedrest:http"));

}
