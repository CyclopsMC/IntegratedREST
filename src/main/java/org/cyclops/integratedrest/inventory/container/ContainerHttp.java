package org.cyclops.integratedrest.inventory.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import org.cyclops.cyclopscore.helper.ValueNotifierHelpers;
import org.cyclops.cyclopscore.inventory.slot.SlotRemoveOnly;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.inventory.container.ContainerActiveVariableBase;
import org.cyclops.integrateddynamics.core.inventory.container.slot.SlotVariable;
import org.cyclops.integratedrest.RegistryEntries;
import org.cyclops.integratedrest.tileentity.TileHttp;

import java.util.Objects;
import java.util.Optional;

/**
 * Container for the http block.
 * @author rubensworks
 */
public class ContainerHttp extends ContainerActiveVariableBase<TileHttp> {

    private final int valueTypeId;

    public ContainerHttp(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(TileHttp.INVENTORY_SIZE), Optional.empty());
    }

    public ContainerHttp(int id, PlayerInventory playerInventory, IInventory inventory,
                         Optional<TileHttp> tileSupplier) {
        super(RegistryEntries.CONTAINER_HTTP, id, playerInventory, inventory, tileSupplier);
        addSlot(new SlotVariable(inventory, TileHttp.SLOT_WRITE_IN, 56, 63));
        addSlot(new SlotRemoveOnly(inventory, TileHttp.SLOT_WRITE_OUT, 104, 63));
        addPlayerInventory(playerInventory, offsetX + 9, offsetY + 92);

        valueTypeId = getNextValueId();
        getTileSupplier().ifPresent(tile -> tile.setLastPlayer(playerInventory.player));
    }

    @Override
    protected void initializeValues() {
        ValueNotifierHelpers.setValue(this, getValueTypeId(), getTileSupplier().get().getValueType().getUniqueName().toString());
    }

    public int getValueTypeId() {
        return valueTypeId;
    }

    public Optional<IValueType> getValueType() {
        String id = ValueNotifierHelpers.getValueString(this, getValueTypeId());
        return id == null ? Optional.empty() : Optional.of(Objects.requireNonNull(ValueTypes.REGISTRY.getValueType(new ResourceLocation(id)), id));
    }

    @Override
    public void onUpdate(int valueId, CompoundNBT value) {
        super.onUpdate(valueId, value);
        if(getTileSupplier().isPresent()) {
            if (valueId == getValueTypeId()) {
                getValueType().ifPresent(vt -> getTileSupplier().get().setValueType(vt));
            }
        }
    }
}
