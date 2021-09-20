package org.cyclops.integratedrest.json;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTypes;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.cyclops.cyclopscore.helper.BlockHelpers;
import org.cyclops.cyclopscore.helper.FluidHelpers;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import org.cyclops.integrateddynamics.core.evaluate.variable.*;
import org.cyclops.integratedrest.IntegratedRest;
import org.cyclops.integratedrest.Uris;
import org.cyclops.integratedrest.api.json.IValueTypeJsonHandlerRegistry;
import org.cyclops.integratedrest.json.handler.CheckedValueTypeJsonHandlerBase;
import org.cyclops.integratedrest.json.handler.TypedObjectValueTypeJsonHandlerBase;

import java.util.List;
import java.util.UUID;

/**
 * Registration code for value type JSON handlers.
 * @author rubensworks
 */
public class ValueTypeJsonHandlers {

    public static IValueTypeJsonHandlerRegistry REGISTRY = IntegratedRest._instance.getRegistryManager().getRegistry(IValueTypeJsonHandlerRegistry.class);

    public static void load() {
        REGISTRY.registerHandler(ValueTypes.BOOLEAN, value -> new JsonPrimitive(value.getRawValue()));
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueTypeBoolean.ValueBoolean>() {
            @Override
            public ValueTypeBoolean.ValueBoolean handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
                return jsonElement instanceof JsonPrimitive && ((JsonPrimitive) jsonElement).isBoolean()
                        ? ValueTypeBoolean.ValueBoolean.of(jsonElement.getAsBoolean()) : null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.INTEGER, value -> new JsonPrimitive(value.getRawValue()));
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueTypeInteger.ValueInteger>() {
            @Override
            public ValueTypeInteger.ValueInteger handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
                return jsonElement instanceof JsonPrimitive && ((JsonPrimitive) jsonElement).isNumber()
                    ? ValueTypeInteger.ValueInteger.of(jsonElement.getAsInt()) : null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.DOUBLE, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", Uris.XSD_DECIMAL);
            jsonObject.addProperty("@value", value.getRawValue());
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new TypedObjectValueTypeJsonHandlerBase<ValueTypeDouble.ValueDouble>(Uris.XSD_DECIMAL) {
            @Override
            protected ValueTypeDouble.ValueDouble handleTypedValueString(String valueString) {
                return ValueTypeDouble.ValueDouble.of(Double.valueOf(valueString));
            }
        });

        REGISTRY.registerHandler(ValueTypes.LONG, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", Uris.XSD_LONG);
            jsonObject.addProperty("@value", value.getRawValue());
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new TypedObjectValueTypeJsonHandlerBase<ValueTypeLong.ValueLong>(Uris.XSD_LONG) {
            @Override
            protected ValueTypeLong.ValueLong handleTypedValueString(String valueString) {
                return ValueTypeLong.ValueLong.of(Long.valueOf(valueString));
            }
        });

        REGISTRY.registerHandler(ValueTypes.STRING, value -> new JsonPrimitive(value.getRawValue()));
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueTypeString.ValueString>() {
            @Override
            public ValueTypeString.ValueString handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
                return ValueTypeString.ValueString.of(jsonElement.getAsString());
            }
        });

        REGISTRY.registerHandler(ValueTypes.LIST, value -> {
            if (value.getRawValue().isInfinite()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("@type", "ValueInfiniteList");
                return jsonObject;
            } else {
                JsonArray jsonArray = new JsonArray();
                for (IValue v : (IValueTypeListProxy<?, IValue>) value.getRawValue()) {
                    jsonArray.add(JsonUtil.valueToJson(v));
                }
                return jsonArray;
            }

        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueTypeList.ValueList>() {
            @Override
            public ValueTypeList.ValueList handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonArray) {
                    List<IValue> elements = Lists.newArrayList();
                    IValueType type = ValueTypes.CATEGORY_ANY;
                    JsonArray jsonArray = (JsonArray) jsonElement;
                    for (JsonElement element : jsonArray) {
                        JsonUtil.jsonToValue(element).ifPresent(elements::add);
                    }
                    if (elements.size() > 0) {
                        IValueType maybeType = elements.get(0).getType();
                        if(elements.stream().map(IValue::getType).allMatch(maybeType::equals)) {
                            // If all of the elements are the same type, then use that type as the list's type.
                            // Otherwise, use Any as the list's type.
                            type = maybeType;
                        }
                    }
                    return ValueTypeList.ValueList.ofList(type, elements);
                }
                return null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.OPERATOR, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", "ValueOperator");
            jsonObject.addProperty("@value", value.getRawValue().getUniqueName().toString());
            return jsonObject;
        });
        // No reverse handler

        REGISTRY.registerHandler(ValueTypes.NBT, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", "ValueNbt");
            jsonObject.add("nbt", new JsonParser().parse(value.getRawValue().orElse(new CompoundNBT()).toString()));
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueTypeNbt.ValueNbt>() {
            @Override
            public ValueTypeNbt.ValueNbt handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("@type") && ((JsonObject) jsonElement).get("@type").getAsString().equals("ValueNbt")) {
                    try {
                        return ValueTypeNbt.ValueNbt.of(JsonToNBT.getTagFromJson(((JsonObject) jsonElement).get("nbt").toString()));
                    } catch (CommandSyntaxException e) {
                        throw new IllegalStateException(e);
                    }
                }
                return null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.OBJECT_BLOCK, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", "ValueBlock");
            if (value.getRawValue().isPresent()) {
                BlockState blockState = value.getRawValue().get();
                jsonObject.addProperty("block", JsonUtil.absolutizePath("registry/block/" + JsonUtil.resourceLocationToPath(blockState.getBlock().getRegistryName())));
                jsonObject.addProperty("resourceLocation", blockState.getBlock().getRegistryName().toString());
                jsonObject.addProperty("state", BlockHelpers.serializeBlockState(blockState).toString());
                JsonArray jsonProperties = new JsonArray();
                for (Property<?> property : blockState.getProperties()) {
                    JsonObject jsonProperty = new JsonObject();
                    jsonProperty.addProperty("key", property.getName());
                    jsonProperty.addProperty("value", blockState.get(property).toString());
                    jsonProperties.add(jsonProperty);
                }
            }
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueObjectTypeBlock.ValueBlock>() {
            @Override
            public ValueObjectTypeBlock.ValueBlock handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("@type") && ((JsonObject) jsonElement).get("@type").getAsString().equals("ValueBlock")) {
                    JsonObject jsonObject = (JsonObject) jsonElement;
                    if (!jsonObject.has("resourceLocation")) {
                        return ValueObjectTypeBlock.ValueBlock.of(null);
                    } else {
                        ResourceLocation resourceLocation = new ResourceLocation(jsonObject.get("resourceLocation").getAsString());
                        Block block = ForgeRegistries.BLOCKS.getValue(resourceLocation);
                        if (block != null) {
                            try {
                                return ValueObjectTypeBlock.ValueBlock.of(BlockHelpers.deserializeBlockState(JsonToNBT.getTagFromJson(jsonObject.get("state").getAsString())));
                            } catch (CommandSyntaxException e) {
                                throw new IllegalStateException(e);
                            }
                        }
                    }
                }
                return null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.OBJECT_ITEMSTACK, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", "ValueItem");
            if (!value.getRawValue().isEmpty()) {
                ItemStack itemStack = value.getRawValue();
                jsonObject.addProperty("item", JsonUtil.absolutizePath("registry/item/" + JsonUtil.resourceLocationToPath(itemStack.getItem().getRegistryName())));
                jsonObject.addProperty("resourceLocation", itemStack.getItem().getRegistryName().toString());
                jsonObject.addProperty("count", itemStack.getCount());
                if (itemStack.hasTag()) {
                    jsonObject.add("nbt", new JsonParser().parse(itemStack.getTag().toString()));
                }
            }
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueObjectTypeItemStack.ValueItemStack>() {
            @Override
            public ValueObjectTypeItemStack.ValueItemStack handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("@type") && ((JsonObject) jsonElement).get("@type").getAsString().equals("ValueItem")) {
                    JsonObject jsonObject = (JsonObject) jsonElement;
                    if (!jsonObject.has("resourceLocation")) {
                        return ValueObjectTypeItemStack.ValueItemStack.of(ItemStack.EMPTY);
                    } else {
                        ResourceLocation resourceLocation = new ResourceLocation(jsonObject.get("resourceLocation").getAsString());
                        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
                        if (item != null) {
                            int count = 1;
                            if (jsonObject.has("count")) {
                                count = jsonObject.get("count").getAsInt();
                            }

                            int meta = 0;
                            if (jsonObject.has("meta")) {
                                meta = jsonObject.get("meta").getAsInt();
                            }

                            ItemStack itemStack = new ItemStack(item, count);
                            if (jsonObject.has("nbt")) {
                                CompoundNBT tag;
                                try {
                                    tag = JsonToNBT.getTagFromJson(jsonObject.get("nbt").toString());
                                } catch (CommandSyntaxException e) {
                                    throw new IllegalStateException(e);
                                }
                                itemStack.setTag(tag);
                            }
                            return ValueObjectTypeItemStack.ValueItemStack.of(itemStack);
                        }
                    }
                }
                return null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.OBJECT_ENTITY, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", "ValueEntity");
            if (value.getUuid().isPresent()) {
                jsonObject.addProperty("uuid", value.getUuid().get().toString());
            }
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueObjectTypeEntity.ValueEntity>() {
            @Override
            public ValueObjectTypeEntity.ValueEntity handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("@type") && ((JsonObject) jsonElement).get("@type").getAsString().equals("ValueEntity")) {
                    if (((JsonObject) jsonElement).has("uuid")) {
                        try {
                            String uuid = ((JsonObject) jsonElement).get("uuid").getAsString();
                            return ValueObjectTypeEntity.ValueEntity.of(UUID.fromString(uuid));
                        } catch (IllegalArgumentException e) {
                            throw new IllegalStateException(e);
                        }
                    } else {
                        return ValueObjectTypeEntity.ValueEntity.of((UUID) null);
                    }
                }
                return null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.OBJECT_FLUIDSTACK, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", "ValueFluid");
            if (!value.getRawValue().isEmpty()) {
                FluidStack fluidStack = value.getRawValue();
                jsonObject.addProperty("fluid", JsonUtil.absolutizePath("registry/fluid/" + fluidStack.getFluid().getRegistryName()));
                jsonObject.addProperty("fluidName", fluidStack.getFluid().getRegistryName().toString());
                jsonObject.addProperty("count", fluidStack.getAmount());
                if (fluidStack.hasTag()) {
                    jsonObject.add("nbt", new JsonParser().parse(fluidStack.getTag().toString()));
                }
            }
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueObjectTypeFluidStack.ValueFluidStack>() {
            @Override
            public ValueObjectTypeFluidStack.ValueFluidStack handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("@type") && ((JsonObject) jsonElement).get("@type").getAsString().equals("ValueFluid")) {
                    JsonObject jsonObject = (JsonObject) jsonElement;
                    if (!jsonObject.has("fluidName")) {
                        return ValueObjectTypeFluidStack.ValueFluidStack.of(FluidStack.EMPTY);
                    } else {
                        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(jsonObject.get("fluidName").getAsString()));
                        if (fluid != null) {
                            int count = FluidHelpers.BUCKET_VOLUME;
                            if (jsonObject.has("count")) {
                                count = jsonObject.get("count").getAsInt();
                            }

                            FluidStack fluidStack = new FluidStack(fluid, count);
                            if (jsonObject.has("nbt")) {
                                CompoundNBT tag;
                                try {
                                    tag = JsonToNBT.getTagFromJson(jsonObject.get("nbt").toString());
                                } catch (CommandSyntaxException e) {
                                    throw new IllegalStateException(e);
                                }
                                fluidStack.setTag(tag);
                            }
                            return ValueObjectTypeFluidStack.ValueFluidStack.of(fluidStack);
                        }
                    }
                }
                return null;
            }
        });
    }

}
