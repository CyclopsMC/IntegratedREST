package org.cyclops.integratedrest.tileentity;

import lombok.Setter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import org.cyclops.cyclopscore.capability.item.ItemHandlerSlotMasked;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.expression.VariableAdapter;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.evaluate.variable.IVariable;
import org.cyclops.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import org.cyclops.integrateddynamics.api.network.INetworkElement;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import org.cyclops.integrateddynamics.capability.networkelementprovider.NetworkElementProviderSingleton;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueHelpers;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.tileentity.TileProxy;
import org.cyclops.integratedrest.RegistryEntries;
import org.cyclops.integratedrest.api.item.IHttpVariableFacade;
import org.cyclops.integratedrest.evaluate.HttpVariableFacadeHandler;
import org.cyclops.integratedrest.inventory.container.ContainerHttp;
import org.cyclops.integratedrest.item.HttpVariableFacade;
import org.cyclops.integratedrest.network.HttpNetworkElement;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * A tile entity for the http block.
 *
 * @author rubensworks
 */
public class TileHttp extends TileProxy {

    public static final int INVENTORY_SIZE = 2;
    public static final int SLOT_WRITE_IN = 0;
    public static final int SLOT_WRITE_OUT = 1;

    private final HttpVariableAdapter variable;

    @Setter
    private PlayerEntity lastPlayer = null;

    public TileHttp() {
        super(RegistryEntries.TILE_ENTITY_HTTP, TileHttp.INVENTORY_SIZE);

        addCapabilitySided(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.NORTH,
                LazyOptional.of(() -> new ItemHandlerSlotMasked(getInventory(), SLOT_WRITE_IN)));
        addCapabilitySided(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.SOUTH,
                LazyOptional.of(() -> new ItemHandlerSlotMasked(getInventory(), SLOT_WRITE_IN)));
        addCapabilitySided(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.EAST,
                LazyOptional.of(() -> new ItemHandlerSlotMasked(getInventory(), SLOT_WRITE_IN)));
        addCapabilitySided(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.WEST,
                LazyOptional.of(() -> new ItemHandlerSlotMasked(getInventory(), SLOT_WRITE_IN)));
        addCapabilitySided(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP,
                LazyOptional.of(() -> new ItemHandlerSlotMasked(getInventory(), SLOT_WRITE_IN)));
        addCapabilitySided(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN,
                LazyOptional.of(() -> new ItemHandlerSlotMasked(getInventory(), SLOT_WRITE_OUT)));

        addCapabilityInternal(NetworkElementProviderConfig.CAPABILITY, LazyOptional.of(() -> new NetworkElementProviderSingleton() {
            @Override
            public INetworkElement createNetworkElement(World world, BlockPos blockPos) {
                return new HttpNetworkElement(DimPos.of(world, blockPos));
            }
        }));

        this.variable = new HttpVariableAdapter(this, ValueTypes.CATEGORY_ANY, ValueTypeBoolean.ValueBoolean.of(false));
    }

    @Override
    public ItemStack writeProxyInfo(boolean generateId, ItemStack itemStack, final int proxyId) {
        IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager().getRegistry(IVariableFacadeHandlerRegistry.class);
        return registry.writeVariableFacadeItem(generateId, itemStack, HttpVariableFacadeHandler.getInstance(), new IVariableFacadeHandlerRegistry.IVariableFacadeFactory<IHttpVariableFacade>() {
            @Override
            public IHttpVariableFacade create(boolean generateId) {
                return new HttpVariableFacade(generateId, proxyId);
            }

            @Override
            public IHttpVariableFacade create(int id) {
                return new HttpVariableFacade(id, proxyId);
            }
        }, lastPlayer, getBlockState());
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
    public CompoundNBT save(CompoundNBT tag) {
        tag = super.save(tag);
        tag.putString("valueType", this.variable.getValueTypeRaw().getUniqueName().toString());
        if (this.variable.getValueRaw() != null) {
            tag.put("value", ValueHelpers.serialize(this.variable.getValueRaw()));
        }
        return tag;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        this.variable.setValueTypeRaw(ValueTypes.REGISTRY.getValueType(new ResourceLocation(tag.getString("valueType"))));
        if (tag.contains("value", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT valueTag = tag.getCompound("value");
            setValue(ValueHelpers.deserialize(valueTag));
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
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new ContainerHttp(id, playerInventory, this.getInventory(), Optional.of(this));
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.integratedrest.http");
    }

    public static class HttpVariableAdapter extends VariableAdapter {
        private final TileHttp tile;
        private IValueType valueType;
        private IValue value;

        public HttpVariableAdapter(TileHttp tile, IValueType valueType, IValue value) {
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
                throw new EvaluationException(new TranslationTextComponent("http.integratedrest.error.http_invalid", tile.getProxyId()));
            }
            return value;
        }
    }
}
