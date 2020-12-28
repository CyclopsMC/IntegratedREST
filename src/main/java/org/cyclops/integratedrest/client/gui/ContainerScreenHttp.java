package org.cyclops.integratedrest.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.cyclops.cyclopscore.client.gui.component.input.WidgetArrowedListField;
import org.cyclops.cyclopscore.helper.ValueNotifierHelpers;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.core.client.gui.ContainerScreenActiveVariableBase;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.logicprogrammer.LogicProgrammerElementTypes;
import org.cyclops.integratedrest.Reference;
import org.cyclops.integratedrest.inventory.container.ContainerHttp;

import java.util.List;

/**
 * Gui for the http block.
 * @author rubensworks
 */
public class ContainerScreenHttp extends ContainerScreenActiveVariableBase<ContainerHttp> {

    private static final int ERROR_X = 140;
    private static final int ERROR_Y = 36;

    private WidgetArrowedListField<IValueType> valueTypeSelector = null;

    public ContainerScreenHttp(ContainerHttp container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/http.png");
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
    public void init() {
        super.init();

        List<IValueType> valueTypes = Lists.newArrayList(LogicProgrammerElementTypes.VALUETYPE.getValueTypes());
        valueTypes.add(ValueTypes.CATEGORY_ANY);
        valueTypeSelector = new WidgetArrowedListField<>(font,
                guiLeft + 38, guiTop + 18, 105, 14, true, new StringTextComponent(""), true, valueTypes);
        valueTypeSelector.setListener(() -> ValueNotifierHelpers.setValue(getContainer(), getContainer().getValueTypeId(), valueTypeSelector.getActiveElement().getUniqueName().toString()));
        getContainer().getValueType().ifPresent(vt -> valueTypeSelector.setActiveElement(vt));
        children.add(valueTypeSelector);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        valueTypeSelector.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onUpdate(int valueId, CompoundNBT value) {
        if (valueId == getContainer().getValueTypeId()) {
            getContainer().getValueType().ifPresent(vt -> valueTypeSelector.setActiveElement(vt));
        }
    }
}
