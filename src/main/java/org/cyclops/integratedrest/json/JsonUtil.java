package org.cyclops.integratedrest.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.IValueInterface;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.evaluate.variable.IVariable;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.INetworkElement;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetworkElement;
import org.cyclops.integrateddynamics.api.network.IPositionedNetworkElement;
import org.cyclops.integrateddynamics.api.network.ISidedNetworkElement;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integratedrest.Capabilities;
import org.cyclops.integratedrest.api.json.IValueTypeJsonHandler;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Utility methods for converting objects to JSON.
 * @author rubensworks
 */
public class JsonUtil {

    // TODO: make @ids URIs

    public static void addNetworkInfo(JsonObject jsonObject, INetwork network) {
        jsonObject.addProperty("@id", Integer.toString(network.hashCode()));
        IPartNetwork partNetwork = NetworkHelpers.getPartNetwork(network);
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

    public static void addNetworkElementInfo(JsonObject jsonObject, INetworkElement networkElement) {
        JsonArray types = new JsonArray();
        types.add("NetworkElement");
        if (networkElement instanceof IPartNetworkElement) {
            types.add("PartNetworkElement");
        } else {
            DimPos pos = getNetworkElementPosition(networkElement);
            if (pos != null) {
                Block block = pos.getWorld().getBlockState(pos.getBlockPos()).getBlock();
                types.add(block.getRegistryName().toString()); // TODO: make URI
            }
        }
        jsonObject.add("@type", types);
        jsonObject.addProperty("channel", networkElement.getChannel());
        jsonObject.addProperty("priority", networkElement.getPriority());
        jsonObject.addProperty("updateInterval", networkElement.getUpdateInterval());

        IValueInterface valueInterface = getNetworkElementCapability(networkElement, Capabilities.VALUE_INTERFACE);
        if (valueInterface != null) {
            JsonUtil.addValueInterfaceInfo(jsonObject, valueInterface);
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
            EnumFacing side = ((ISidedNetworkElement) networkElement).getSide();
            return PartPos.of(pos, side);
        }
        return null;
    }

    @Nullable
    public static <T> T getNetworkElementCapability(INetworkElement networkElement, Capability<T> capability) {
        PartPos partPos = getNetworkElementPositionSided(networkElement);
        if (partPos != null) {
            return TileHelpers.getCapability(partPos.getPos(), partPos.getSide(), capability);
        }
        DimPos pos = getNetworkElementPosition(networkElement);
        if (pos != null) {
            return TileHelpers.getCapability(pos, capability);
        }
        return null;
    }

    public static void addPartNetworkElementInfo(JsonObject jsonObject, IPartNetworkElement<?, ?> networkElement, IPartNetwork partNetwork) {
        jsonObject.addProperty("@id", Integer.toString(networkElement.getPartState().getId()));
        jsonObject.add("center", partPosToJson(networkElement.getTarget().getCenter()));
        jsonObject.add("target", partPosToJson(networkElement.getTarget().getTarget()));
        jsonObject.addProperty("loaded", networkElement.isLoaded());
        ((JsonArray) jsonObject.get("@type")).add(networkElement.getPart().getUnlocalizedNameBase());

        // TODO: rm?
        /*IPartType partType = networkElement.getPart();
        if (partType instanceof IPartTypeActiveVariable) {
            IVariable<?> variable = ((IPartTypeActiveVariable) partType).getActiveVariable(partNetwork, networkElement.getTarget(), networkElement.getPartState());
            if (variable != null) {
                JsonObject jsonVariable = new JsonObject();
                addVariableInfo(jsonVariable, variable);
                jsonObject.add("value", jsonVariable);
            }
        }*/
    }

    public static void addValueInterfaceInfo(JsonObject jsonObject, IValueInterface valueInterface) {
        try {
            Optional<IValue> value = valueInterface.getValue();
            if (value.isPresent()) {
                JsonObject jsonVariable = new JsonObject();
                addValueInfo(jsonVariable, value.get());;
                jsonObject.add("value", jsonVariable);
            }
        } catch (EvaluationException e) {
            jsonObject.addProperty("error", e.getMessage());
        }
    }

    public static JsonObject partPosToJson(PartPos partPos) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", partPos.getPos().getDimensionId());
        jsonObject.addProperty("x", partPos.getPos().getBlockPos().getX());
        jsonObject.addProperty("y", partPos.getPos().getBlockPos().getY());
        jsonObject.addProperty("z", partPos.getPos().getBlockPos().getZ());
        jsonObject.addProperty("side", partPos.getSide().name());
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
        IValueType valueType = value.getType();
        jsonObject.addProperty("@type", valueType.getUnlocalizedName());
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

}
