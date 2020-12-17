package com.kennie.nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import com.kennie.nettyServer.enums.MsgTypeEnum;
import com.kennie.nettyServer.proto.SmartCarProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("p2p")
public class P2pMsgStrategy extends BaseStrategy implements BaseStrategyInterface {

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Override
    public void updateConversationAndsaveMsg(ChannelHandlerContext ctx, JSONObject msgJson) {
        /**
         * 发送消息到存储队列 p2pMsg-s-p
         * 存储服务进行存储并添加未读（或是记录最后一条消息）
         */
//        kafkaTemplate.send("p2pMsg-s-p",msgJson.toJSONString());
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
        Long toUid = msgJson.getLong("toUid");

        Optional<ChannelId> channelId = Optional.ofNullable(cmap.get(toUid));
        //单机版 或者对方就在本机
        if (channelId.isPresent()) {
            String reqChannel = reqCmap.get(toUid);
            if("ws".equals(reqChannel)){
                channels.find(channelId.get()).writeAndFlush(new TextWebSocketFrame(msgJson.toJSONString()));
            }else {
                byte[] msgByte = JSONObject.toJSONString(msgJson).getBytes();
                SmartCarProtocol msg = new SmartCarProtocol(msgByte.length,msgByte);
                channels.find(channelId.get()).writeAndFlush(msg);
            }
            //重投机制
        }else {
            /**
             * 往kafka发送消息 p2pMsg-p
             * 分发服务消费消息 根据redis在线群id进行kafka分发 p2pMsg-consumer-{ip}
             */
            kafkaTemplate.send("p2pMsg-p",msgJson.toJSONString());
        }
    }
}
