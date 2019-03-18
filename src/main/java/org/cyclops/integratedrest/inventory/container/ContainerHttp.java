package org.cyclops.integratedrest.inventory.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.cyclops.cyclopscore.helper.ValueNotifierHelpers;
import org.cyclops.cyclopscore.inventory.slot.SlotRemoveOnly;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.inventory.container.ContainerActiveVariableBase;
import org.cyclops.integrateddynamics.core.inventory.container.slot.SlotVariable;
import org.cyclops.integratedrest.tileentity.TileHttp;

/**
 * Container for the http block.
 * @author rubensworks
 */
public class ContainerHttp extends ContainerActiveVariableBase<TileHttp> {

    private final int valueTypeId;

    /**
     * Make a new instance.
     * @param inventory The player inventory.
     * @param tile The part.
     */
    public ContainerHttp(InventoryPlayer inventory, TileHttp tile) {
        super(inventory, tile);
        addSlotToContainer(new SlotVariable(tile, TileHttp.SLOT_WRITE_IN, 56, 63));
        addSlotToContainer(new SlotRemoveOnly(tile, TileHttp.SLOT_WRITE_OUT, 104, 63));
        addPlayerInventory(inventory, offsetX + 9, offsetY + 92);

        valueTypeId = getNextValueId();
        tile.setLastPlayer(inventory.player);
    }

    @Override
    protected void initializeValues() {
        ValueNotifierHelpers.setValue(this, getValueTypeId(), getTile().getValueType().getTranslationKey());
    }

    public int getValueTypeId() {
        return valueTypeId;
    }

    public IValueType getValueType() {
        return ValueTypes.REGISTRY.getValueType(ValueNotifierHelpers.getValueString(this, getValueTypeId()));
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        super.onUpdate(valueId, value);
        if(!getTile().getWorld().isRemote) {
            if (valueId == getValueTypeId()) {
                getTile().setValueType(getValueType());
            }
        }
    }
}
