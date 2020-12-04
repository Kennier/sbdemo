package com.kennie.nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;

public interface BaseStrategyInterface {

    default void updateConversationAndsaveMsg(ChannelHandlerContext ctx, JSONObject msgJson){};//往mq发

    default void sendMsg(ChannelHandlerContext ctx, JSONObject msgJson){};

    default void msgAck(ChannelHandlerContext ctx, JSONObject msgJson){};
}
