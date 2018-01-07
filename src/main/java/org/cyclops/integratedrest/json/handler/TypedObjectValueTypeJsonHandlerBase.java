package org.cyclops.integratedrest.json.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;

/**
 * @author rubensworks
 */
public abstract class TypedObjectValueTypeJsonHandlerBase<V extends IValue> extends CheckedValueTypeJsonHandlerBase<V> {

    private final String type;

    public TypedObjectValueTypeJsonHandlerBase(String type) {
        this.type = type;
    }

    protected abstract V handleTypedValueString(String valueString);

    @Override
    public V handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("@type") && jsonObject.has("@value")) {
                if (jsonObject.getAsJsonPrimitive("@type").getAsString().equals(this.type)) {
                    return handleTypedValueString(jsonObject.getAsJsonPrimitive("@value").getAsString());
                }
            }
        }
        return null;
    }
}
