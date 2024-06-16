package org.cyclops.integratedrest.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforgespi.language.IModInfo;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.helper.BlockEntityHelpers;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.integrateddynamics.Capabilities;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.IValueInterface;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.evaluate.variable.IVariable;
import org.cyclops.integrateddynamics.api.network.IIdentifiableNetworkElement;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.INetworkElement;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetworkElement;
import org.cyclops.integrateddynamics.api.network.IPositionedNetworkElement;
import org.cyclops.integrateddynamics.api.network.ISidedNetworkElement;
import org.cyclops.integrateddynamics.api.part.IPartState;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.api.part.aspect.IAspectRead;
import org.cyclops.integrateddynamics.api.part.aspect.IAspectWrite;
import org.cyclops.integrateddynamics.api.part.read.IPartTypeReader;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integratedrest.GeneralConfig;
import org.cyclops.integratedrest.api.json.IReverseValueTypeJsonHandler;
import org.cyclops.integratedrest.api.json.IValueTypeJsonHandler;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Optional;

/**
 * Utility methods for converting objects to JSON.
 * @author rubensworks
 */
public class JsonUtil {

    public static String absolutizePath(String path) {
        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }
        return GeneralConfig.apiBaseUrl + path;
    }

    public static void addNetworkInfo(JsonObject jsonObject, INetwork network) {
        jsonObject.addProperty("@id", JsonUtil.absolutizePath("network/" + Integer.toString(network.hashCode())));
        IPartNetwork partNetwork = NetworkHelpers.getPartNetworkChecked(network);
        JsonArray types = new JsonArray();
        types.add("Network");
        if (partNetwork != null) {
            types.add("PartNetwork");
        }
        jsonObject.add("@type", types);
        jsonObject.addProperty("cableCount", network.getCablesCount());
        jsonObject.addProperty("elementCount", network.getElements().size());
        jsonObject.addProperty("crashed", network.isCrashed());
        jsonObject.addProperty("initialized", network.isInitialized());
    }

    public static void addNetworkElementInfo(JsonObject jsonObject, INetworkElement networkElement, INetwork network) {
        IPartNetwork partNetwork = NetworkHelpers.getPartNetworkChecked(network);
        JsonArray types = new JsonArray();
        types.add("NetworkElement");

        if (networkElement instanceof IIdentifiableNetworkElement) {
            IIdentifiableNetworkElement identifiable = (IIdentifiableNetworkElement) networkElement;
            jsonObject.addProperty("@id", JsonUtil.absolutizePath("networkElement/" + resourceLocationToPath(identifiable.getGroup()) + "/" + identifiable.getId()));
        }

        if (networkElement instanceof IPositionedNetworkElement) {
            Direction side = null;
            if (networkElement instanceof ISidedNetworkElement) {
                side = ((ISidedNetworkElement) networkElement).getSide();
            }
            jsonObject.add("position", posToJson(((IPositionedNetworkElement) networkElement).getPosition(), side));
        }

        DimPos pos = getNetworkElementPosition(networkElement);
        if (pos != null) {
            Block block = pos.getLevel(true).getBlockState(pos.getBlockPos()).getBlock();
            jsonObject.addProperty("block", JsonUtil.absolutizePath("registry/block/" + JsonUtil.resourceLocationToPath(BuiltInRegistries.BLOCK.getKey(block))));
        }

        jsonObject.add("@type", types);
        jsonObject.addProperty("channel", networkElement.getChannel());
        jsonObject.addProperty("priority", networkElement.getPriority());
        jsonObject.addProperty("updateInterval", networkElement.getUpdateInterval());
        jsonObject.addProperty("network", JsonUtil.absolutizePath("network/" + network.hashCode()));

        getNetworkElementCapability(networkElement, Capabilities.ValueInterface.BLOCK)
                .ifPresent(valueInterface -> JsonUtil.addValueInterfaceInfo(jsonObject, valueInterface));

        if (partNetwork != null && networkElement instanceof IPartNetworkElement) {
            JsonUtil.addPartNetworkElementInfo(jsonObject, (IPartNetworkElement<?, ?>) networkElement, partNetwork);
        }
    }

    @Nullable
    public static DimPos getNetworkElementPosition(INetworkElement networkElement) {
        if (networkElement instanceof IPositionedNetworkElement) {
            return ((IPositionedNetworkElement) networkElement).getPosition();
        }
        return null;
    }

    @Nullable
    public static PartPos getNetworkElementPositionSided(INetworkElement networkElement) {
        if (networkElement instanceof ISidedNetworkElement) {
            DimPos pos = getNetworkElementPosition(networkElement);
            Direction side = ((ISidedNetworkElement) networkElement).getSide();
            return PartPos.of(pos, side);
        }
        return null;
    }

    public static <T> Optional<T> getNetworkElementCapability(INetworkElement networkElement, BlockCapability<T, Direction> capability) {
        PartPos partPos = getNetworkElementPositionSided(networkElement);
        if (partPos != null) {
            return BlockEntityHelpers.getCapability(partPos.getPos(), partPos.getSide(), capability);
        }
        DimPos pos = getNetworkElementPosition(networkElement);
        if (pos != null) {
            return BlockEntityHelpers.getCapability(pos, capability);
        }
        return Optional.empty();
    }

    public static void addPartNetworkElementInfo(JsonObject jsonObject, IPartNetworkElement<?, ?> networkElement, IPartNetwork partNetwork) {
        jsonObject.add("target", partPosToJson(networkElement.getTarget().getTarget()));
        jsonObject.addProperty("loaded", networkElement.isLoaded());
        ((JsonArray) jsonObject.get("@type")).add(JsonUtil.absolutizePath("registry/part/" + JsonUtil.resourceLocationToPath(networkElement.getPart().getUniqueName())));

        IPartState partState = networkElement.getPartState();
        if (partState instanceof IPartStateWriter && ((IPartStateWriter) partState).getActiveAspect() != null) {
            JsonObject object = new JsonObject();
            addAspectTypeInfo(object, ((IPartStateWriter) partState).getActiveAspect());
            jsonObject.add("activeAspect", object);
        }
    }

    public static void addValueInterfaceInfo(JsonObject jsonObject, IValueInterface valueInterface) {
        try {
            Optional<IValue> value = valueInterface.getValue();
            if (value.isPresent()) {
                addValueInfo(jsonObject, value.get());
            }
        } catch (EvaluationException e) {
            jsonObject.addProperty("error", e.getMessage());
        }
    }

    public static JsonObject partPosToJson(PartPos partPos) {
        return posToJson(partPos.getPos(), partPos.getSide());
    }

    public static JsonObject posToJson(DimPos pos, @Nullable Direction side) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", JsonUtil.resourceLocationToPath(pos.getLevelKey().location()));
        jsonObject.addProperty("x", pos.getBlockPos().getX());
        jsonObject.addProperty("y", pos.getBlockPos().getY());
        jsonObject.addProperty("z", pos.getBlockPos().getZ());
        if (side != null) {
            jsonObject.addProperty("side", "ir:" + side.name().toLowerCase(Locale.ENGLISH));
        }
        return jsonObject;
    }

    public static void addVariableInfo(JsonObject jsonObject, IVariable variable) {
        try {
            addValueInfo(jsonObject, variable.getValue());
        } catch (EvaluationException e) {
            jsonObject.addProperty("error", e.getMessage());
        }
    }

    public static void addValueInfo(JsonObject jsonObject, IValue value) {
        jsonObject.addProperty("valueType", JsonUtil.absolutizePath("registry/value/" + JsonUtil.resourceLocationToPath(value.getType().getUniqueName())));
        jsonObject.add("value", valueToJson(value));
    }

    public static JsonElement valueToJson(IValue value) {
        IValueTypeJsonHandler<IValue> valueTypeJsonHandler = ValueTypeJsonHandlers.REGISTRY.getHandler(value.getType());
        if (valueTypeJsonHandler != null) {
            return valueTypeJsonHandler.handle(value);
        } else {
            return new JsonObject();
        }
    }

    public static Optional<IValue> jsonToValue(JsonElement jsonElement) {
        for (IReverseValueTypeJsonHandler<?> handler : ValueTypeJsonHandlers.REGISTRY.getReverseHandlers()) {
            IValue value = handler.handle(jsonElement);
            if (value != null) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public static String resourceLocationToPath(ResourceLocation resourceLocation) {
        return resourceLocation.getNamespace() + "/" + resourceLocation.getPath();
    }

    public static void addPartTypeInfo(JsonObject jsonObject, IPartType partType) {
        jsonObject.addProperty("@id", JsonUtil.absolutizePath("registry/part/" + JsonUtil.resourceLocationToPath(partType.getUniqueName())));
        jsonObject.addProperty("resourceLocation", partType.getUniqueName().toString());
        JsonArray types = new JsonArray();
        if (partType instanceof IPartTypeReader) {
            types.add("ReadPart");
        }
        if (partType instanceof IPartTypeWriter) {
            types.add("WritePart");
        }
        jsonObject.add("@type", types);
        jsonObject.addProperty("item", JsonUtil.absolutizePath("registry/item/" + JsonUtil.resourceLocationToPath(BuiltInRegistries.ITEM.getKey(partType.getItem()))));
        if (partType instanceof IPartTypeReader) {
            JsonArray array = new JsonArray();
            for (IAspect aspect : ((IPartTypeReader<?, ?>) partType).getReadAspects()) {
                JsonObject object = new JsonObject();
                addAspectTypeInfo(object, aspect);
                array.add(object);
            }
            jsonObject.add("readAspects", array);
        }
        if (partType instanceof IPartTypeWriter) {
            JsonArray array = new JsonArray();
            for (IAspect aspect : ((IPartTypeWriter<?, ?>) partType).getWriteAspects()) {
                JsonObject object = new JsonObject();
                addAspectTypeInfo(object, aspect);
                array.add(object);
            }
            jsonObject.add("writeAspects", array);
        }
    }

    public static void addAspectTypeInfo(JsonObject jsonObject, IAspect aspect) {
        jsonObject.addProperty("@id", JsonUtil.absolutizePath("registry/aspect/" + JsonUtil.resourceLocationToPath(aspect.getUniqueName())));
        jsonObject.addProperty("resourceLocation", aspect.getUniqueName().toString());
        JsonArray types = new JsonArray();
        if (aspect instanceof IAspectRead) {
            types.add("ReadAspect");
        }
        if (aspect instanceof IAspectWrite) {
            types.add("WriteAspect");
        }
        jsonObject.add("@type", types);
        jsonObject.addProperty("label", L10NHelpers.localize(aspect.getTranslationKey()));
        jsonObject.addProperty("comment", L10NHelpers.localize(aspect.getTranslationKey() + ".info"));
        jsonObject.addProperty("unlocalizedName", aspect.getTranslationKey());
        jsonObject.addProperty("valueType", JsonUtil.absolutizePath("registry/value/" + aspect.getValueType().getTranslationKey().replace('.', '/')));
    }

    public static void addValueTypeInfo(JsonObject jsonObject, IValueType valueType) {
        jsonObject.addProperty("@id", JsonUtil.absolutizePath("registry/value/" + JsonUtil.resourceLocationToPath(valueType.getUniqueName())));
        jsonObject.addProperty("resourceLocation", valueType.getUniqueName().toString());
        jsonObject.addProperty("label", valueType.getTypeName());
        jsonObject.addProperty("unlocalizedName", valueType.getTranslationKey());
        jsonObject.add("value", JsonUtil.valueToJson(valueType.getDefault()));
        jsonObject.addProperty("color", valueType.getDisplayColor());
    }

    public static void addItemInfo(JsonObject jsonObject, Item item) {
        jsonObject.addProperty("@id", JsonUtil.absolutizePath("registry/item/" + JsonUtil.resourceLocationToPath(BuiltInRegistries.ITEM.getKey(item))));
        jsonObject.addProperty("mod", JsonUtil.absolutizePath("registry/mod/" + BuiltInRegistries.ITEM.getKey(item).getNamespace()));
        jsonObject.addProperty("unlocalizedName", item.getDescriptionId());
        jsonObject.addProperty("resourceLocation", BuiltInRegistries.ITEM.getKey(item).toString());
        Block block = Block.byItem(item);
        if (block != null && block != Blocks.AIR) {
            jsonObject.addProperty("block", JsonUtil.absolutizePath("registry/block/" + JsonUtil.resourceLocationToPath(BuiltInRegistries.BLOCK.getKey(block))));
        }
    }

    public static void addBlockInfo(JsonObject jsonObject, Block block) {
        jsonObject.addProperty("@id", JsonUtil.absolutizePath("registry/block/" + JsonUtil.resourceLocationToPath(BuiltInRegistries.BLOCK.getKey(block))));
        jsonObject.addProperty("mod", JsonUtil.absolutizePath("registry/mod/" + BuiltInRegistries.BLOCK.getKey(block).getNamespace()));
        jsonObject.addProperty("unlocalizedName", block.getDescriptionId());
        jsonObject.addProperty("resourceLocation", BuiltInRegistries.BLOCK.getKey(block).toString());
        Item item = Item.byBlock(block);
        if (item != null && item != Items.AIR) {
            jsonObject.addProperty("item", JsonUtil.absolutizePath("registry/item/" + JsonUtil.resourceLocationToPath(BuiltInRegistries.ITEM.getKey(item))));
        }
    }

    public static void addFluidInfo(JsonObject jsonObject, Fluid fluid) {
        jsonObject.addProperty("@id", JsonUtil.absolutizePath("registry/fluid/" + JsonUtil.resourceLocationToPath(BuiltInRegistries.FLUID.getKey(fluid))));
        jsonObject.addProperty("resourceLocation", BuiltInRegistries.FLUID.getKey(fluid).toString());
        if (fluid.defaultFluidState().createLegacyBlock() != null) {
            jsonObject.addProperty("block", JsonUtil.absolutizePath("registry/block/" + JsonUtil.resourceLocationToPath(BuiltInRegistries.BLOCK.getKey(fluid.defaultFluidState().createLegacyBlock().getBlock()))));
        }
    }

    public static void addModInfo(JsonObject jsonObject, IModInfo modInfo) {
        jsonObject.addProperty("@id", JsonUtil.absolutizePath("registry/mod/" + modInfo.getModId()));
        jsonObject.addProperty("label", modInfo.getDisplayName());
        jsonObject.addProperty("version", modInfo.getVersion().toString());

        JsonArray dependencies = new JsonArray();
        for (IModInfo.ModVersion modVersion : modInfo.getDependencies()) {
            JsonObject jsonVersion = new JsonObject();
            addDependencyInfo(jsonVersion, modVersion);
            dependencies.add(jsonVersion);
        }
        jsonObject.add("dependencies", dependencies);
    }

    public static void addDependencyInfo(JsonObject jsonObject, IModInfo.ModVersion modVersion) {
        jsonObject.addProperty("mod", JsonUtil.absolutizePath("registry/mod/" + modVersion.getModId()));
        jsonObject.addProperty("versionRange", modVersion.getVersionRange().toString());
    }

}
