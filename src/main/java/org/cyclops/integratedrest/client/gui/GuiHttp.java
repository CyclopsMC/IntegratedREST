package org.cyclops.integratedrest.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.cyclops.cyclopscore.client.gui.component.input.GuiArrowedListField;
import org.cyclops.cyclopscore.client.gui.component.input.GuiNumberField;
import org.cyclops.cyclopscore.helper.Helpers;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.helper.ValueNotifierHelpers;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.block.BlockDelayConfig;
import org.cyclops.integrateddynamics.core.client.gui.GuiActiveVariableBase;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.logicprogrammer.LogicProgrammerElementTypes;
import org.cyclops.integrateddynamics.inventory.container.ContainerDelay;
import org.cyclops.integratedrest.inventory.container.ContainerHttp;
import org.cyclops.integratedrest.tileentity.TileHttp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gui for the http block.
 * @author rubensworks
 */
public class GuiHttp extends GuiActiveVariableBase<ContainerHttp, TileHttp> {

    private static final int ERROR_X = 140;
    private static final int ERROR_Y = 36;

    private GuiArrowedListField<IValueType> valueTypeSelector = null;

    /**
     * Make a new instance.
     * @param inventory The player inventory.
     * @param tile The part.
     */
    public GuiHttp(InventoryPlayer inventory, TileHttp tile) {
        super(new ContainerHttp(inventory, tile));
    }

    @Override
    protected int getBaseYSize() {
        return 173;
    }

    @Override
    protected int getErrorX() {
        return ERROR_X;
    }

    @Override
    protected int getErrorY() {
        return ERROR_Y;
    }

    @Override
    protected int getValueY() {
        return 42;
    }

    @Override
    public void initGui() {
        super.initGui();

        List<IValueType> valueTypes = Lists.newArrayList(LogicProgrammerElementTypes.VALUETYPE.getValueTypes());
        valueTypes.add(ValueTypes.CATEGORY_ANY);
        valueTypeSelector = new GuiArrowedListField<>(0, Minecraft.getMinecraft().fontRenderer,
                guiLeft + 38, guiTop + 18, 105, 14, true, true, valueTypes);
        valueTypeSelector.setListener(() -> ValueNotifierHelpers.setValue(getContainer(), getContainer().getValueTypeId(), valueTypeSelector.getActiveElement().getUnlocalizedName()));
        valueTypeSelector.setActiveElement(getContainer().getValueType());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        valueTypeSelector.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        valueTypeSelector.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        if (valueId == getContainer().getValueTypeId()) {
            valueTypeSelector.setActiveElement(getContainer().getValueType());
        }
    }
}
