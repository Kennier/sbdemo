package com.kennie.nettyServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class SimpleChatServerInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline channelPipeline = socketChannel.pipeline();
        //这里使用自定义分隔符
        ByteBuf delimiter = Unpooled.copiedBuffer("\r\n".getBytes());

        channelPipeline
//                .addLast("HttpServerCodec",new HttpServerCodec())
//                .addLast(new HttpObjectAggregator(1024 * 64))
////                .addLast("decoder", new StringDecoder())
////                .addLast("encoder", new StringEncoder())
//                .addLast(new WebSocketServerProtocolHandler("/ws"))
//                .addLast("selfHandler", new SimpleChatServerHandler());
//                .addLast("idleStateHandler", new IdleStateHandler(60, 0, 0))
//                .addLast("idleStateTrigger", new ServerIdleStateTrigger())

//                .addLast(new DelimiterBasedFrameDecoder(8192, delimiter))
                .addLast("decoder",new SmartCarDecoder())
                .addLast("encoder",new SmartCarEncoder())
                .addLast(new ServerHandler());

        System.out.println("SimpleChatClient:"+socketChannel.remoteAddress() +"连接上");
    }
}
