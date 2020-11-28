package nettyServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    public void start() throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup(2);
        NioEventLoopGroup work = new NioEventLoopGroup(8);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new SimpleChatServerInitializer());

            System.out.println("SimpleChatServer 启动了");

            ChannelFuture f = serverBootstrap.bind(8093).sync();

            f.channel().closeFuture().sync();
        } finally {

            work.shutdownGracefully();
            boss.shutdownGracefully();

            System.out.println("SimpleChatServer 关闭了");
        }

    }

    public static void main(String[] args) throws InterruptedException {
        new NettyServer().start();
    }
}
