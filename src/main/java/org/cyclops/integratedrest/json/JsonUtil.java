package org.cyclops.integratedrest.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.evaluate.variable.IVariable;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.INetworkElement;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetworkElement;
import org.cyclops.integrateddynamics.api.part.IPartState;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.part.PartStateActiveVariableBase;
import org.cyclops.integratedrest.api.json.IValueTypeJsonHandler;

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
        }
        jsonObject.add("@type", types);
        jsonObject.addProperty("channel", networkElement.getChannel());
        jsonObject.addProperty("priority", networkElement.getPriority());
        jsonObject.addProperty("updateInterval", networkElement.getUpdateInterval());
    }

    public static void addPartNetworkElementInfo(JsonObject jsonObject, IPartNetworkElement<?, ?> networkElement, IPartNetwork partNetwork) {
        jsonObject.addProperty("@id", Integer.toString(networkElement.getPartState().getId()));
        jsonObject.add("center", partPosToJson(networkElement.getTarget().getCenter()));
        jsonObject.add("target", partPosToJson(networkElement.getTarget().getTarget()));
        jsonObject.addProperty("loaded", networkElement.isLoaded());
        ((JsonArray) jsonObject.get("@type")).add(networkElement.getPart().getUnlocalizedNameBase());

        IPartState partState = networkElement.getPartState();
        if (partState instanceof PartStateActiveVariableBase) {
            IVariable variable = ((PartStateActiveVariableBase) partState).getVariable(partNetwork);
            if (variable != null) {
                JsonObject jsonVariable = new JsonObject();
                addVariableInfo(jsonVariable, variable);
                jsonObject.add("value", jsonVariable);
            }
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
            IValue value = variable.getValue();
            IValueType valueType = value.getType();
            jsonObject.addProperty("@type", valueType.getUnlocalizedName());
            jsonObject.add("value", valueToJson(value));
        } catch (EvaluationException e) {
            jsonObject.addProperty("error", e.getMessage());
        }
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
