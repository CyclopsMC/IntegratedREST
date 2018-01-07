package org.cyclops.integratedrest.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeDouble;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeInteger;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeString;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integratedrest.IntegratedRest;
import org.cyclops.integratedrest.Uris;
import org.cyclops.integratedrest.api.json.IValueTypeJsonHandlerRegistry;
import org.cyclops.integratedrest.json.handler.CheckedValueTypeJsonHandlerBase;
import org.cyclops.integratedrest.json.handler.TypedObjectValueTypeJsonHandlerBase;

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
