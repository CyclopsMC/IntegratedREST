package org.cyclops.integratedrest.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integratedrest.IntegratedRest;
import org.cyclops.integratedrest.api.json.IValueTypeJsonHandlerRegistry;

/**
 * Registration code for value type JSON handlers.
 * @author rubensworks
 */
public class ValueTypeJsonHandlers {

    public static IValueTypeJsonHandlerRegistry REGISTRY = IntegratedRest._instance.getRegistryManager().getRegistry(IValueTypeJsonHandlerRegistry.class);

    public static void load() {
        REGISTRY.registerHandler(ValueTypes.BOOLEAN, value -> new JsonPrimitive(value.getRawValue()));
        REGISTRY.registerHandler(ValueTypes.INTEGER, value -> new JsonPrimitive(value.getRawValue()));
        REGISTRY.registerHandler(ValueTypes.DOUBLE, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", "http://www.w3.org/2001/XMLSchema#decimal");
            jsonObject.addProperty("@value", value.getRawValue());
            return jsonObject;
        });
        REGISTRY.registerHandler(ValueTypes.LONG, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", "http://www.w3.org/2001/XMLSchema#long");
            jsonObject.addProperty("@value", value.getRawValue());
            return jsonObject;
        });
        REGISTRY.registerHandler(ValueTypes.STRING, value -> new JsonPrimitive(value.getRawValue()));
        // TODO: support more complex value types
        /*REGISTRY.registerHandler(ValueTypes.LIST, value -> new JsonPrimitive(value.getRawValue()));
        REGISTRY.registerHandler(ValueTypes.OPERATOR, value -> new JsonPrimitive(value.getRawValue()));
        REGISTRY.registerHandler(ValueTypes.NBT, value -> new JsonPrimitive(value.getRawValue()));

        REGISTRY.registerHandler(ValueTypes.OBJECT_BLOCK, value -> new JsonPrimitive(value.getRawValue()));
        REGISTRY.registerHandler(ValueTypes.OBJECT_ITEMSTACK, value -> new JsonPrimitive(value.getRawValue()));
        REGISTRY.registerHandler(ValueTypes.OBJECT_ENTITY, value -> new JsonPrimitive(value.getRawValue()));
        REGISTRY.registerHandler(ValueTypes.OBJECT_FLUIDSTACK, value -> new JsonPrimitive(value.getRawValue()));*/
    }

}
