package com.sunchangpeng.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class EchoClient extends Lifecycle {
    private NioEventLoopGroup eventLoopGroup;
    private Bootstrap client;
    private Channel clientChannel;

    public EchoClient() {
        eventLoopGroup = new NioEventLoopGroup();
        client = new Bootstrap().group(eventLoopGroup).channel(NioSocketChannel.class)
                .remoteAddress("127.0.0.1", 8080)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ClientHandler());
                    }
                });
    }

    @Override
    public void doStart() {
        ChannelFuture channelFuture = client.connect().syncUninterruptibly();
        clientChannel = channelFuture.channel();
    }

    @Override
    public void doShutDown() {
        clientChannel.closeFuture().syncUninterruptibly();
        eventLoopGroup.shutdownGracefully().syncUninterruptibly();
    }

    private static class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
        private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(Unpooled.copiedBuffer("Hello Netty!", StandardCharsets.UTF_8));
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
            System.out.println("client received: " + byteBuf.toString(StandardCharsets.UTF_8));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("exceptionCaught", cause);
            ctx.close();
        }
    }
}
