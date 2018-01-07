package org.cyclops.integratedrest.api.json;

import com.google.gson.JsonElement;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;

import javax.annotation.Nullable;

/**
 * Handler for converting a value from JSON.
 * @author rubensworks
 */
public interface IReverseValueTypeJsonHandler<V extends IValue> {

    /**
     * Convert the given value from JSON.
     * @param jsonElement A JSON element.
     * @return A value, or null if it can't be handled.
     */
    @Nullable
    public V handle(JsonElement jsonElement);

}
