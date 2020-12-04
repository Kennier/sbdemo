package com.kennie.nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import com.kennie.nettyServer.proto.SmartCarProtocol;

public class BindMsgStrategy extends BaseStrategy implements BaseStrategyInterface {

    @Override
    public void msgAck(ChannelHandlerContext ctx, JSONObject msgJson) {
        System.out.println("发送服务端ACK消息");
        msgJson.put("channelId",ctx.channel().id().asLongText());
        String ackString = msgJson.toJSONString();
        SmartCarProtocol ack = new SmartCarProtocol(ackString.getBytes().length,ackString.getBytes());
        ctx.channel().writeAndFlush(ack);
    }

}
