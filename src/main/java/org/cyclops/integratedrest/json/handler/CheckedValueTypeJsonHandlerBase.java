package org.cyclops.integratedrest.json.handler;

import com.google.gson.JsonElement;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integratedrest.api.json.IReverseValueTypeJsonHandler;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public abstract class CheckedValueTypeJsonHandlerBase<V extends IValue> implements IReverseValueTypeJsonHandler<V> {

    public abstract V handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException;

    @Nullable
    @Override
    public V handle(JsonElement jsonElement) {
        try {
            return handleUnchecked(jsonElement);
        } catch (IllegalStateException | ClassCastException | NumberFormatException | UnsupportedOperationException e) {
            return null;
        }
    }
}
