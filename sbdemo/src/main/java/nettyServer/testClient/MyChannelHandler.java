package nettyServer.testClient;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * 网络事件处理器
 */
public class MyChannelHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 添加自定义协议的编解码工具
        ch.pipeline().addLast(new SmartCarDecoder());
        ch.pipeline().addLast(new SmartCarDecoder());
        // 处理网络IO
        ch.pipeline().addLast(new ClientHandler());
    }

}