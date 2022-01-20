package org.cyclops.integratedrest.http.request.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import org.cyclops.cyclopscore.helper.BlockEntityHelpers;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.INetworkElement;
import org.cyclops.integrateddynamics.api.network.IPositionedNetworkElement;
import org.cyclops.integratedrest.blockentity.BlockEntityHttp;
import org.cyclops.integratedrest.json.JsonUtil;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Request handler for /element/http requests.
 * @author rubensworks
 */
public class ElementHttpRequestHandler extends ElementTypeRequestHandler {
    @Nullable
    @Override
    protected HttpResponseStatus handleElement(int id, INetwork network, INetworkElement networkElement,
                                               HttpRequest request, JsonObject responseObject) {
        if (networkElement instanceof IPositionedNetworkElement) {
            IPositionedNetworkElement positionedNetworkElement = (IPositionedNetworkElement) networkElement;
            BlockEntityHttp tile = BlockEntityHelpers.get(positionedNetworkElement.getPosition(), BlockEntityHttp.class).orElse(null);
            if (tile != null) {
                if (tile.getProxyId() == id) {
                    if (request.method().equals(HttpMethod.GET)) {
                        JsonUtil.addNetworkElementInfo(responseObject, networkElement, network);
                        responseObject.addProperty("http://www.w3.org/ns/ldp#inbox", responseObject.get("@id").getAsString());
                        return HttpResponseStatus.OK;
                    } else if (request.method().equals(HttpMethod.POST) && request instanceof HttpContent) {
                        String content = ((HttpContent) request).content().toString(CharsetUtil.UTF_8);
                        try {
                            JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
                            JsonElement jsonElement = jsonObject.get("value");
                            if (jsonElement == null) {
                                responseObject.addProperty("error", "No value property was found in the root object.");
                                return HttpResponseStatus.BAD_REQUEST;
                            }
                            Optional<IValue> valueOptional = JsonUtil.jsonToValue(jsonElement);
                            if (valueOptional.isPresent()) {
                                IValue value = valueOptional.get();
                                if (tile.getValueType().correspondsTo(value.getType())) {
                                    tile.setValue(valueOptional.get());
                                } else {
                                    responseObject.addProperty("error", "Invalid value type, HTTP Proxy expects " + tile.getValueType().getTypeName() + " but " + value.getType().getTypeName() + " was given.");
                                    return HttpResponseStatus.BAD_REQUEST;
                                }
                            } else {
                                responseObject.addProperty("error", "No valid value was given.");
                                return HttpResponseStatus.BAD_REQUEST;
                            }
                        } catch (JsonSyntaxException | ClassCastException e) {
                            responseObject.addProperty("error", e.getMessage());
                            return HttpResponseStatus.BAD_REQUEST;
                        }
                        responseObject.addProperty("ok", "Value was successfully updated.");
                        return HttpResponseStatus.OK;
                    } else {
                        return HttpResponseStatus.METHOD_NOT_ALLOWED;
                    }
                }
            }
        }
        return null;
    }
}
