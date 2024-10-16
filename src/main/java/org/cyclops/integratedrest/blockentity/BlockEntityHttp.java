package org.cyclops.integratedrest.blockentity;

import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.cyclops.cyclopscore.capability.item.ItemHandlerSlotMasked;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.Capabilities;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.expression.VariableAdapter;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.evaluate.variable.IVariable;
import org.cyclops.integrateddynamics.api.evaluate.variable.ValueDeseralizationContext;
import org.cyclops.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import org.cyclops.integrateddynamics.api.network.INetworkElement;
import org.cyclops.integrateddynamics.api.network.INetworkElementProvider;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.blockentity.BlockEntityProxy;
import org.cyclops.integrateddynamics.capability.networkelementprovider.NetworkElementProviderSingleton;
import org.cyclops.integrateddynamics.core.blockentity.BlockEntityCableConnectableInventory;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueHelpers;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integratedrest.RegistryEntries;
import org.cyclops.integratedrest.api.item.IHttpVariableFacade;
import org.cyclops.integratedrest.evaluate.HttpVariableFacadeHandler;
import org.cyclops.integratedrest.inventory.container.ContainerHttp;
import org.cyclops.integratedrest.item.HttpVariableFacade;
import org.cyclops.integratedrest.network.HttpNetworkElement;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A tile entity for the http block.
 *
 * @author rubensworks
 */
public class BlockEntityHttp extends BlockEntityProxy {

    public static final int INVENTORY_SIZE = 2;
    public static final int SLOT_WRITE_IN = 0;
    public static final int SLOT_WRITE_OUT = 1;

    private final HttpVariableAdapter variable;

    @Setter
    private Player lastPlayer = null;

    public BlockEntityHttp(BlockPos blockPos, BlockState blockState) {
        super(RegistryEntries.BLOCK_ENTITY_HTTP.get(), blockPos, blockState, BlockEntityHttp.INVENTORY_SIZE);
        this.variable = new HttpVariableAdapter(this, ValueTypes.CATEGORY_ANY, ValueTypeBoolean.ValueBoolean.of(false));
    }

    public static class CapabilityRegistrar extends BlockEntityCableConnectableInventory.CapabilityRegistrar<BlockEntityHttp> {
        public CapabilityRegistrar(Supplier<BlockEntityType<? extends BlockEntityHttp>> blockEntityType) {
            super(blockEntityType);
        }

        @Override
        public void populate() {
            super.populate();

            add(
                    net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                    (blockEntity, direction) -> {
                        int slot = -1;
                        switch (direction) {
                            case DOWN ->  slot = SLOT_WRITE_OUT;
                            case UP ->    slot = SLOT_WRITE_IN;
                            case NORTH -> slot = SLOT_WRITE_IN;
                            case SOUTH -> slot = SLOT_WRITE_IN;
                            case WEST ->  slot = SLOT_WRITE_IN;
                            case EAST ->  slot = SLOT_WRITE_IN;
                        }
                        return new ItemHandlerSlotMasked(blockEntity.getInventory(), slot);
                    }
            );
            add(
                    Capabilities.NetworkElementProvider.BLOCK,
                    (blockEntity, direction) -> blockEntity.getNetworkElementProvider()
            );
        }
    }

    @Override
    public INetworkElementProvider getNetworkElementProvider() {
        return new NetworkElementProviderSingleton() {
            @Override
            public INetworkElement createNetworkElement(Level world, BlockPos blockPos) {
                return new HttpNetworkElement(DimPos.of(world, blockPos));
            }
        };
    }

    @Override
    public ItemStack writeProxyInfo(boolean generateId, ItemStack itemStack, final int proxyId) {
        IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager().getRegistry(IVariableFacadeHandlerRegistry.class);
        return registry.writeVariableFacadeItem(generateId, itemStack, HttpVariableFacadeHandler.getInstance(), new IVariableFacadeHandlerRegistry.IVariableFacadeFactory<>() {
            @Override
            public IHttpVariableFacade create(boolean generateId) {
                return new HttpVariableFacade(generateId, proxyId);
            }

            @Override
            public IHttpVariableFacade create(int id) {
                return new HttpVariableFacade(id, proxyId);
            }
        }, getLevel(), lastPlayer, getBlockState());
    }

    @Override
    public IVariable<?> getVariable(IPartNetwork network) {
        return variable;
    }

    @Override
    public int getSlotRead() {
        return -1;
    }

    @Override
    protected int getSlotWriteIn() {
        return SLOT_WRITE_IN;
    }

    @Override
    protected int getSlotWriteOut() {
        return SLOT_WRITE_OUT;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putString("valueType", this.variable.getValueTypeRaw().getUniqueName().toString());
        if (this.variable.getValueRaw() != null) {
            tag.put("value", ValueHelpers.serialize(ValueDeseralizationContext.of(provider), this.variable.getValueRaw()));
        }
    }

    @Override
    public void read(CompoundTag tag, HolderLookup.Provider provider) {
        super.read(tag, provider);
        this.variable.setValueTypeRaw(ValueTypes.REGISTRY.getValueType(ResourceLocation.parse(tag.getString("valueType"))));
        if (tag.contains("value", Tag.TAG_COMPOUND)) {
            CompoundTag valueTag = tag.getCompound("value");
            setValue(ValueHelpers.deserialize(ValueDeseralizationContext.of(getLevel()), valueTag));
        }
    }

    public IValueType<IValue> getValueType() {
        return this.variable.getValueTypeRaw();
    }

    public void setValueType(IValueType valueType) {
        this.variable.setValueTypeRaw(valueType);
        if (!valueType.isCategory()) {
            setValue(valueType.getDefault());
        } else {
            setValue(ValueTypeBoolean.ValueBoolean.of(false));
        }
        sendUpdate();
    }

    public void setValue(IValue value) {
        this.variable.invalidate();
        this.variable.setValueRaw(value);
        sendUpdate();
    }

    @Override
    public boolean hasVariable() {
        return this.variable.getValueRaw() != null;
    }

    @Override
    protected void updateReadVariable(boolean sendVariablesUpdateEvent) {
        // Do nothing
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity) {
        return new ContainerHttp(id, playerInventory, this.getInventory(), Optional.of(this));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.integratedrest.http");
    }

    public static class HttpVariableAdapter extends VariableAdapter {
        private final BlockEntityHttp tile;
        private IValueType valueType;
        private IValue value;

        public HttpVariableAdapter(BlockEntityHttp tile, IValueType valueType, IValue value) {
            this.tile = tile;
            this.valueType = valueType;
            this.value = value;
        }

        @Nullable
        public IValue getValueRaw() {
            return this.value;
        }

        public IValueType getValueTypeRaw() {
            return valueType;
        }

        public void setValueRaw(IValue value) {
            this.value = value;
        }

        public void setValueTypeRaw(IValueType valueType) {
            this.valueType = valueType;
        }

        @Override
        public IValueType getType() {
            return this.valueType;
        }

        @Override
        public IValue getValue() throws EvaluationException {
            if (value == null) {
                throw new EvaluationException(Component.translatable("http.integratedrest.error.http_invalid", tile.getProxyId()));
            }
            return value;
        }
    }
}
