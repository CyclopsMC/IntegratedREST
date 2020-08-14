package org.cyclops.integratedrest.http;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integratedrest.Uris;
import org.cyclops.integratedrest.api.http.request.IRequestHandler;
import org.cyclops.integratedrest.http.request.RequestHandlers;

import java.util.List;

/**
 * A handler for HTTP requests.
 * @author rubensworks
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Object message) throws Exception {
        if (message instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) message;

            if (HttpUtil.is100ContinueExpected(request)) {
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
                context.write(response);
            }

            JsonObject responseObject = new JsonObject();
            responseObject.addProperty("@context", Uris.IT + "context.jsonld");

            String[] path = request.uri().substring(1).split("/");
            List<String[]> paths = Lists.newArrayList();
            while (path.length > 1) {
                paths.add(path);
                String prefix = path[0];
                path = ArrayUtils.subarray(path, 1, path.length);
                path[0] = prefix + '/' + path[0];
            }
            paths.add(path);
            paths = Lists.reverse(paths); // Longest match first

            IRequestHandler requestHandler = null;
            for (String[] pathArray : paths) {
                requestHandler = RequestHandlers.REGISTRY.getHandler(pathArray[0]);
                if (requestHandler != null) {
                    path = pathArray;
                    break;
                }
            }

            HttpResponseStatus responseStatus;
            if (requestHandler == null) {
                responseStatus = HttpResponseStatus.NOT_FOUND;
            } else {
                TileHelpers.UNSAFE_TILE_ENTITY_GETTER = true;
                responseStatus = requestHandler.handle(ArrayUtils.subarray(path, 1, path.length), request, responseObject);
                TileHelpers.UNSAFE_TILE_ENTITY_GETTER = false;
            }

            if (responseStatus == HttpResponseStatus.NOT_FOUND) {
                responseObject.addProperty("error", "Resource was not found.");
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String responseString = gson.toJson(responseObject) + "\n";
            if (!writeResponse(request, context, responseString, responseStatus)) {
                // If keep-alive is off, close the connection once the content is fully written.
                context.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private boolean writeResponse(HttpRequest request, ChannelHandlerContext context, String responseString,
                                  HttpResponseStatus responseStatus) {
        // Decide whether to close the connection or not.
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, responseStatus,
                Unpooled.copiedBuffer(responseString, CharsetUtil.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        // Write the response.
        context.write(response);

        return keepAlive;
    }
}
