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
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.INetworkElement;
import org.cyclops.integrateddynamics.api.network.IPositionedNetworkElement;
import org.cyclops.integratedrest.json.JsonUtil;
import org.cyclops.integratedrest.tileentity.TileHttp;

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
            TileHttp tile = TileHelpers.getSafeTile(positionedNetworkElement.getPosition(), TileHttp.class);
            if (tile != null) {
                if (tile.getProxyId() == id) {
                    if (request.method().equals(HttpMethod.GET)) {
                        JsonUtil.addNetworkElementInfo(responseObject, networkElement, network);
                        responseObject.addProperty("http://www.w3.org/ns/ldp#inbox", "THIS"); // TODO: add element URI
                        return HttpResponseStatus.OK;
                    } else if (request.method().equals(HttpMethod.POST) && request instanceof HttpContent) {
                        String content = ((HttpContent) request).content().toString(CharsetUtil.UTF_8);
                        try {
                            JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
                            JsonElement jsonElement = jsonObject.get("value");
                            if (jsonElement == null) {
                                return HttpResponseStatus.BAD_REQUEST;
                            }
                            Optional<IValue> valueOptional = JsonUtil.jsonToValue(jsonElement);
                            if (valueOptional.isPresent()) {
                                IValue value = valueOptional.get();
                                if (tile.getValueType().correspondsTo(value.getType())) {
                                    tile.setValue(valueOptional.get());
                                } else {
                                    return HttpResponseStatus.BAD_REQUEST;
                                }
                            } else {
                                return HttpResponseStatus.BAD_REQUEST;
                            }
                        } catch (JsonSyntaxException | ClassCastException e) {
                            return HttpResponseStatus.BAD_REQUEST;
                        }
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
