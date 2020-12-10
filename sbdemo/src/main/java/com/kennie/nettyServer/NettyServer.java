package com.kennie.nettyServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(name = "baseStrategy")
public class NettyServer implements CommandLineRunner {

    @Autowired
    SimpleChatServerInitializer simpleChatServerInitializer;

    @Value("${netty.port}")
    String nettyPort;

    @Override
    public void run(String... args) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup(2);
        NioEventLoopGroup work = new NioEventLoopGroup(8);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(simpleChatServerInitializer);

            System.out.println("SimpleChatServer 启动了");

            ChannelFuture f = serverBootstrap.bind(Integer.parseInt(nettyPort)).sync();

            f.channel().closeFuture().sync();
        } finally {

            work.shutdownGracefully();
            boss.shutdownGracefully();

            System.out.println("SimpleChatServer 关闭了");
        }

    }

    public static void main(String[] args) throws InterruptedException {
        new NettyServer().run();
    }

}
