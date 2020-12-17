package com.kennie.nettyServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
//@Scope("prototype")
//@ChannelHandler.Sharable
public class SimpleChatServerInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    ServerHandler serverHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline channelPipeline = socketChannel.pipeline();
        //这里使用自定义分隔符
        ByteBuf delimiter = Unpooled.copiedBuffer("\r\n".getBytes());

        channelPipeline
////                .addLast("decoder", new StringDecoder())
////                .addLast("encoder", new StringEncoder())
//                .addLast("selfHandler", new SimpleChatServerHandler());
                .addLast("idleStateHandler", new IdleStateHandler(10, 0, 0))
                .addLast("idleStateTrigger", new ServerIdleStateTrigger())

//                .addLast(new DelimiterBasedFrameDecoder(8192, delimiter))
                //websocket
                .addLast("socketChoose",new SocketChooseHandler())

//                .addLast("myDecoder",new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN,1024*1024,0,4,0,4,true))
                //通用和tcp
                .addLast("decoder",new SmartCarDecoder())
                .addLast("encoder",new SmartCarEncoder())
                .addLast("commonhandler",serverHandler);

        System.out.println("SimpleChatClient:"+socketChannel.remoteAddress() +"连接上");
    }
}
