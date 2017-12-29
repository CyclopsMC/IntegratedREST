package org.cyclops.integratedrest.api.json;

import com.google.gson.JsonElement;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;

/**
 * Handler for converting a value to JSON.
 * @author rubensworks
 */
public interface IValueTypeJsonHandler<V extends IValue> {

    /**
     * Convert the given value to JSON.
     * @param value A value.
     * @return A JSON element.
     */
    public JsonElement handle(V value);

}
