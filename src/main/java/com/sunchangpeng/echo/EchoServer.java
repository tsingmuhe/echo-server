package com.sunchangpeng.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class EchoServer extends Lifecycle {
    private static final Logger log = LoggerFactory.getLogger(EchoServer.class);

    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;
    private ServerBootstrap server;
    private ServerHandler serverHandler;
    private Channel serverChannel;

    public EchoServer() {
        boss = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup(4);
        serverHandler = new ServerHandler();
        server = new ServerBootstrap().group(boss, worker).channel(NioServerSocketChannel.class)
                .localAddress(8080)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(serverHandler);
                    }
                });
    }

    @Override
    public void doStart() {
        ChannelFuture channelFuture = server.bind().syncUninterruptibly();
        serverChannel = channelFuture.channel();
        log.info("server started.");
    }

    @Override
    public void doShutDown() {
        serverChannel.closeFuture().syncUninterruptibly();
        boss.shutdownGracefully().syncUninterruptibly();
        worker.shutdownGracefully().syncUninterruptibly();
        log.info("server end.");
    }

    @ChannelHandler.Sharable
    private static class ServerHandler extends ChannelInboundHandlerAdapter {
        private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf in = (ByteBuf) msg;
            System.out.println(in.toString(StandardCharsets.UTF_8));
            ctx.write(in);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("exceptionCaught", cause);
            ctx.close();
        }
    }
}


