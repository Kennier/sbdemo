package com.kennie.nettyServer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kennie.nettyServer.msgStrategy.BaseStrategy;
import com.kennie.nettyServer.proto.SmartCarProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
//@Scope("prototype")
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    @Autowired
    @Qualifier("baseStrategy")
    BaseStrategy baseStrategy;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        Channel in = ctx.channel();
        System.out.println("[SERVER] - " + in.remoteAddress() +":上线    " +"channel's hashCode: "+ in.hashCode());
    }

    //由于继承了SimpleChannelInboundHandler，这个方法必须实现，否则报错
    //但实际应用中，这个方法没被调用
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buff = (ByteBuf) msg;
        String info = buff.toString(CharsetUtil.UTF_8);
        System.out.println("收到消息内容："+info);
    }

    // 用于获取客户端发送的信息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        // WebSocket消息处理
        JSONObject msgJson;
        if (msg instanceof WebSocketFrame) {
            String webSocketInfo = ((TextWebSocketFrame) msg).text().trim();
            System.out.println("收到webSocket消息：" + webSocketInfo);
            msgJson = JSON.parseObject(webSocketInfo);
        }
        // Socket消息处理
        else{
            // 用于获取客户端发来的数据信息
            SmartCarProtocol body = (SmartCarProtocol) msg;
//        System.out.println("Server接受的客户端的信息 :" + body.toString());
            String msgBody = new String(body.getContent(), StandardCharsets.UTF_8);
            System.out.println("Server接受的客户端的信息2 :" + msgBody);
            msgJson = JSON.parseObject(msgBody);

            // 回给发送端的ACK
//        String str = "Hi I am Server ACK";
//        SmartCarProtocol response = new SmartCarProtocol(str.getBytes().length,
//                str.getBytes());
//        // 当服务端完成写操作后，关闭与客户端的连接
//        ctx.writeAndFlush(response);
            // .addListener(ChannelFutureListener.CLOSE);

            // 当有写操作时，不需要手动释放msg的引用
            // 当只有读操作时，才需要手动释放msg的引用
        }
        baseStrategy.handleMsg(ctx,msgJson);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        baseStrategy.removeByChannel(ctx.channel());
        cause.printStackTrace();
        ctx.close();
    }
}
