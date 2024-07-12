package org.cyclops.integratedrest.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
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

    public ContainerScreenHttp(ContainerHttp container, Inventory inventory, Component title) {
        super(container, inventory, title);
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/http.png");
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
                leftPos + 38, topPos + 18, 105, 14, true, Component.literal(""), true, valueTypes);
        valueTypeSelector.setListener(() -> ValueNotifierHelpers.setValue(getMenu(), getMenu().getValueTypeId(), valueTypeSelector.getActiveElement().getUniqueName().toString()));
        getMenu().getValueType().ifPresent(vt -> valueTypeSelector.setActiveElement(vt));
        addWidget(valueTypeSelector);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTicks, mouseX, mouseY);
        valueTypeSelector.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onUpdate(int valueId, CompoundTag value) {
        if (valueId == getMenu().getValueTypeId()) {
            getMenu().getValueType().ifPresent(vt -> valueTypeSelector.setActiveElement(vt));
        }
    }
}
