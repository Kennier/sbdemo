package com.kennie.nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import com.kennie.nettyServer.enums.MsgTypeEnum;
import com.kennie.nettyServer.proto.SmartCarProtocol;

import java.util.Optional;

public class P2pMsgStrategy extends BaseStrategy implements BaseStrategyInterface {

    @Override
    public void updateConversationAndsaveMsg(ChannelHandlerContext ctx, JSONObject msgJson) {
        /**
         * 发送消息到存储队列 p2pMsg-p-s
         * 存储服务进行存储并添加未读（或是记录最后一条消息）
         */
        System.out.println("mq发送消息：保存消息");
    }

    @Override
    public void msgAck(ChannelHandlerContext ctx, JSONObject msgJson) {
        JSONObject ackMsg = new JSONObject();
        ackMsg.put("msgType", MsgTypeEnum.P2P_ACK.getValue());
        ackMsg.put("msgId", msgJson.getString("msgId"));
        ackMsg.put("fromUid", msgJson.getLong("fromUid"));
        ackMsg.put("toUid",msgJson.getLong("toUid"));
        ackMsg.put("createTime", msgJson.getLong("createTime"));
        System.out.println("发送ACK消息"+ackMsg.toJSONString());
        byte[] msgByte = JSONObject.toJSONString(ackMsg).getBytes();
        SmartCarProtocol msg = new SmartCarProtocol(msgByte.length,msgByte);
        ctx.writeAndFlush(msg);
    }

    @Override
    public void sendMsg(ChannelHandlerContext ctx, JSONObject msgJson){
        byte[] msgByte = JSONObject.toJSONString(msgJson).getBytes();
        SmartCarProtocol msg = new SmartCarProtocol(msgByte.length,msgByte);
        Long toUid = msgJson.getLong("toUid");

        Optional<ChannelId> channelId = Optional.ofNullable(BaseStrategy.cmap.get(toUid));
        //单机版 或者对方就在本机
        if (channelId.isPresent()) {
            BaseStrategy.channels.find(channelId.get()).writeAndFlush(msg);
        }else {
            /**
             * 往kafka发送消息 p2pMsg-p
             * 分发服务消费消息 根据redis在线群id进行kafka分发 p2pMsg-consumer-{ip}
             */
        }
    }
}