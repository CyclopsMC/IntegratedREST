package org.cyclops.integratedrest.json;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
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
                jsonObject.addProperty("@type", "InfiniteList");
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
                        type = elements.get(0).getType();
                    }
                    return ValueTypeList.ValueList.ofList(type, elements);
                }
                return null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.OPERATOR, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", "Operator");
            jsonObject.addProperty("@value", value.getRawValue().getUniqueName());
            return jsonObject;
        });
        // No reverse handler

        REGISTRY.registerHandler(ValueTypes.NBT, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("nbt", new JsonParser().parse(value.getRawValue().toString()));
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueTypeNbt.ValueNbt>() {
            @Override
            public ValueTypeNbt.ValueNbt handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("nbt")) {
                    try {
                        return ValueTypeNbt.ValueNbt.of(JsonToNBT.getTagFromJson(((JsonObject) jsonElement).get("nbt").toString()));
                    } catch (NBTException e) {
                        throw new IllegalStateException(e);
                    }
                }
                return null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.OBJECT_BLOCK, value -> {
            JsonObject jsonObject = new JsonObject();
            if (value.getRawValue().isPresent()) {
                IBlockState blockState = value.getRawValue().get();
                jsonObject.addProperty("block", JsonUtil.absolutizePath("registry/block/" + JsonUtil.resourceLocationToPath(blockState.getBlock().getRegistryName())));
                jsonObject.addProperty("blockId", blockState.getBlock().getRegistryName().toString());
                jsonObject.addProperty("meta", blockState.getBlock().getMetaFromState(blockState));
                JsonArray jsonProperties = new JsonArray();
                for (IProperty<?> property : blockState.getPropertyKeys()) {
                    JsonObject jsonProperty = new JsonObject();
                    jsonProperty.addProperty("key", property.getName());
                    jsonProperty.addProperty("value", blockState.getValue(property).toString());
                    jsonProperties.add(jsonProperty);
                }
            } else {
                jsonObject.addProperty("block", "rdf:nil");
                jsonObject.addProperty("blockId", "rdf:nil");
            }
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueObjectTypeBlock.ValueBlock>() {
            @Override
            public ValueObjectTypeBlock.ValueBlock handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("blockId")) {
                    JsonObject jsonObject = (JsonObject) jsonElement;
                    if (jsonObject.get("blockId").getAsString().equals("rdf:nil")) {
                        return ValueObjectTypeBlock.ValueBlock.of(null);
                    } else {
                        ResourceLocation resourceLocation = new ResourceLocation(jsonObject.get("blockId").getAsString());
                        Block block = Block.REGISTRY.getObject(resourceLocation);
                        if (block != null) {
                            return ValueObjectTypeBlock.ValueBlock.of(block.getStateFromMeta(jsonObject.get("meta").getAsInt()));
                        }
                    }
                }
                return null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.OBJECT_ITEMSTACK, value -> {
            JsonObject jsonObject = new JsonObject();
            if (!value.getRawValue().isEmpty()) {
                ItemStack itemStack = value.getRawValue();
                jsonObject.addProperty("item", JsonUtil.absolutizePath("registry/item/" + JsonUtil.resourceLocationToPath(itemStack.getItem().getRegistryName())));
                jsonObject.addProperty("itemId", itemStack.getItem().getRegistryName().toString());
                jsonObject.addProperty("count", itemStack.getCount());
                jsonObject.addProperty("meta", itemStack.getMetadata());
                if (itemStack.hasTagCompound()) {
                    jsonObject.add("tag", new JsonParser().parse(itemStack.getTagCompound().toString()));
                }
            } else {
                jsonObject.addProperty("item", "rdf:nil");
                jsonObject.addProperty("itemId", "rdf:nil");
            }
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueObjectTypeItemStack.ValueItemStack>() {
            @Override
            public ValueObjectTypeItemStack.ValueItemStack handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("itemId")) {
                    JsonObject jsonObject = (JsonObject) jsonElement;
                    if (jsonObject.get("itemId").getAsString().equals("rdf:nil")) {
                        return ValueObjectTypeItemStack.ValueItemStack.of(ItemStack.EMPTY);
                    } else {
                        ResourceLocation resourceLocation = new ResourceLocation(jsonObject.get("itemId").getAsString());
                        Item item = Item.REGISTRY.getObject(resourceLocation);
                        if (item != null) {
                            int count = 1;
                            if (jsonObject.has("count")) {
                                count = jsonObject.get("count").getAsInt();
                            }

                            int meta = 0;
                            if (jsonObject.has("meta")) {
                                meta = jsonObject.get("meta").getAsInt();
                            }

                            ItemStack itemStack = new ItemStack(item, count, meta);
                            if (jsonObject.has("tag")) {
                                NBTTagCompound tag;
                                try {
                                    tag = JsonToNBT.getTagFromJson(jsonObject.get("tag").toString());
                                } catch (NBTException e) {
                                    throw new IllegalStateException(e);
                                }
                                itemStack.setTagCompound(tag);
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
            if (value.getUuid().isPresent()) {
                jsonObject.addProperty("entityUuid", value.getUuid().get().toString());
            } else {
                jsonObject.addProperty("entityUuid", "rdf:nil");
            }
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueObjectTypeEntity.ValueEntity>() {
            @Override
            public ValueObjectTypeEntity.ValueEntity handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("entityUuid")) {
                    String uuid = ((JsonObject) jsonElement).get("entityUuid").getAsString();
                    if (uuid.equals("rdf:nil")) {
                        return ValueObjectTypeEntity.ValueEntity.of((UUID) null);
                    } else {
                        try {
                            return ValueObjectTypeEntity.ValueEntity.of(UUID.fromString(uuid));
                        } catch (IllegalArgumentException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }
                return null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.OBJECT_FLUIDSTACK, value -> {
            JsonObject jsonObject = new JsonObject();
            if (value.getRawValue().isPresent()) {
                FluidStack fluidStack = value.getRawValue().get();
                jsonObject.addProperty("fluid", JsonUtil.absolutizePath("registry/fluid/" + fluidStack.getFluid().getName()));
                jsonObject.addProperty("fluidId", fluidStack.getFluid().getName());
                jsonObject.addProperty("amount", fluidStack.amount);
                if (fluidStack.tag != null) {
                    jsonObject.add("tag", new JsonParser().parse(fluidStack.tag.toString()));
                }
            } else {
                jsonObject.addProperty("fluid", "rdf:nil");
                jsonObject.addProperty("fluidId", "rdf:nil");
            }
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueObjectTypeFluidStack.ValueFluidStack>() {
            @Override
            public ValueObjectTypeFluidStack.ValueFluidStack handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("fluidId")) {
                    JsonObject jsonObject = (JsonObject) jsonElement;
                    if (jsonObject.get("fluidId").getAsString().equals("rdf:nil")) {
                        return ValueObjectTypeFluidStack.ValueFluidStack.of(null);
                    } else {
                        Fluid fluid = FluidRegistry.getFluid(jsonObject.get("fluidId").getAsString());
                        if (fluid != null) {
                            int count = Fluid.BUCKET_VOLUME;
                            if (jsonObject.has("amount")) {
                                count = jsonObject.get("amount").getAsInt();
                            }

                            FluidStack fluidStack = new FluidStack(fluid, count);
                            if (jsonObject.has("tag")) {
                                NBTTagCompound tag;
                                try {
                                    tag = JsonToNBT.getTagFromJson(jsonObject.get("tag").toString());
                                } catch (NBTException e) {
                                    throw new IllegalStateException(e);
                                }
                                fluidStack.tag = tag;
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
