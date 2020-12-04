package com.kennie.nettyServer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import com.kennie.nettyServer.msgStrategy.BaseStrategy;
import com.kennie.nettyServer.proto.SmartCarProtocol;

import java.nio.charset.StandardCharsets;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        Channel in = ctx.channel();
        System.out.println("[SERVER] - " + in.remoteAddress() +":上线    " +"channel's hashCode: "+ in.hashCode());

    }

    // 用于获取客户端发送的信息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        // 用于获取客户端发来的数据信息
        SmartCarProtocol body = (SmartCarProtocol) msg;
//        System.out.println("Server接受的客户端的信息 :" + body.toString());
        String msgBody = new String(body.getContent(), StandardCharsets.UTF_8);
        System.out.println("Server接受的客户端的信息2 :" + msgBody);

        JSONObject msgJson = JSON.parseObject(msgBody);

        BaseStrategy.handleMsg(ctx,msgJson);

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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
