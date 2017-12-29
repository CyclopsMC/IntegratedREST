package org.cyclops.integratedrest.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.Level;
import org.cyclops.integratedrest.GeneralConfig;
import org.cyclops.integratedrest.IntegratedRest;

/**
 * An HTTP server that holds a single channel.
 * @author rubensworks
 */
public class HttpServer {

    private Channel channel;

    public Channel getChannel() {
        return channel;
    }

    public void initialize() {
        IntegratedRest.clog(Level.INFO, "Starting Integrated REST server...");
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpServerInitializer());

        try {
            this.channel = b.bind(GeneralConfig.apiPort).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        IntegratedRest.clog(Level.INFO, "Started Integrated REST server on http://localhost:" + GeneralConfig.apiPort + "/");
    }

    public void deinitialize() {
        IntegratedRest.clog(Level.INFO, "Stopping Integrated REST server...");
        if (this.channel != null) {
            try {
                channel.close().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        IntegratedRest.clog(Level.INFO, "Stopped Integrated REST server");
    }

}
