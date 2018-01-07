package org.cyclops.integratedrest.api.json;

import org.cyclops.cyclopscore.init.IRegistry;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Registry for {@link IValueTypeJsonHandler}s.
 * @author rubensworks
 */
public interface IValueTypeJsonHandlerRegistry extends IRegistry {

    /**
     * Register a handler for the given value type.
     * @param valueType A value type.
     * @param handler A handler.
     * @param <V> The value type.
     * @param <T> The value type type.
     */
    public <V extends IValue, T extends IValueType<V>> void registerHandler(T valueType, IValueTypeJsonHandler<V> handler);

    /**
     * Register a reverse handler for the given value type.
     * @param handler A handler.
     * @param <V> The value type.
     * @param <T> The value type type.
     */
    public <V extends IValue, T extends IValueType<V>> void registerReverseHandler(IReverseValueTypeJsonHandler<V> handler);

    /**
     * Get a handler for the given value type.
     * @param valueType A value type.
     * @param <V> The value type.
     * @param <T> The value type type.
     * @return A handler or null.
     */
    @Nullable
    public <V extends IValue, T extends IValueType<V>> IValueTypeJsonHandler<V> getHandler(T valueType);

    /**
     * @return All reverse handlers.
     */
    @Nullable
    public Collection<IReverseValueTypeJsonHandler<?>> getReverseHandlers();

}
