package com.kennie.nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import com.kennie.nettyServer.enums.MsgTypeEnum;
import com.kennie.nettyServer.proto.SmartCarProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component("channel")
public class ChannelMsgStrategy extends BaseStrategy implements BaseStrategyInterface {

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Override
    public void updateConversationAndsaveMsg(ChannelHandlerContext ctx, JSONObject msgJson) {
        /**
         * 发送消息到存储队列 channelMsg-s-p
         * 存储服务进行存储并添加未读（或是记录群最后一条消息）
         */
//        kafkaTemplate.send("channelMsg-s-p",msgJson);
        System.out.println("mq发送消息：保存消息");
    }

    @Override
    public void msgAck(ChannelHandlerContext ctx, JSONObject msgJson) {
        JSONObject ackMsg = new JSONObject();
        ackMsg.put("msgType", MsgTypeEnum.CHANNEL_ACK.getValue());
        ackMsg.put("msgId", msgJson.getString("msgId"));
        ackMsg.put("fromUid", msgJson.getLong("fromUid"));
        ackMsg.put("toUid", msgJson.getLong("toUid"));
        ackMsg.put("chatroomId",msgJson.getLong("chatroomId"));
        ackMsg.put("createTime", msgJson.getLong("createTime"));
        System.out.println("发送ACK消息"+ackMsg.toJSONString());
        String reqChannel = msgJson.getString("reqChannel");
        if("ws".equals(reqChannel)){
            ctx.writeAndFlush(new TextWebSocketFrame(ackMsg.toJSONString()));
        }else {
            byte[] msgByte = JSONObject.toJSONString(ackMsg).getBytes();
            SmartCarProtocol msg = new SmartCarProtocol(msgByte.length,msgByte);
            ctx.writeAndFlush(msg);
        }
    }

    @Override
    public void sendMsg(ChannelHandlerContext ctx, JSONObject msgJson){

        /**
         * 集群版
         * 往kafka发送消息 channelMsg-p
         * 分发服务消费消息
         * 根据redis在线群id进行kafka分发(和fromUid在同一台机器的不发)到channelMsg-consumer-{ip}
         */
        kafkaTemplate.send("channelMsg-p",msgJson.toJSONString());

//        long chatroomId = msgJson.getLong("chatroomId");
//        long fromUid = msgJson.getLong("fromUid");
//        //redis获取在线群成员及游客????
//        Set<Long> userIds = roomIds.get(chatroomId);
        //单机 或者就在本机
//        for (long uid: userIds){
//            if(uid == fromUid){
//                continue;
//            }
//            msgJson.put("toUid",uid);
//            byte[] msgByte = JSONObject.toJSONString(msgJson).getBytes();
//            SmartCarProtocol msg = new SmartCarProtocol(msgByte.length,msgByte);
//            Optional<ChannelId> channelId = Optional.ofNullable(cmap.get(uid));
//            if (channelId.isPresent()) {
//                channels.find(channelId.get()).writeAndFlush(msg);
//            }
//        }
    }
}
