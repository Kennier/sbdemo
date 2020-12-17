package com.kennie.nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import com.kennie.nettyServer.proto.SmartCarProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component("offline")
public class OfflineMsgStrategy extends BaseStrategy implements BaseStrategyInterface {

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Override
    public void msgAck(ChannelHandlerContext ctx, JSONObject msgJson) {
        String reqChannel = msgJson.getString("reqChannel");
        if("ws".equals(reqChannel)){
            ctx.channel().writeAndFlush(new TextWebSocketFrame(msgJson.toJSONString()));
        }else {
            String ackString = msgJson.toJSONString();
            SmartCarProtocol ack = new SmartCarProtocol(ackString.getBytes().length,ackString.getBytes());
            ctx.channel().writeAndFlush(ack);
        }
        System.out.println("发送服务端ACK消息"+msgJson.toString());
    }

    @Override
    public void sendMsg(ChannelHandlerContext ctx, JSONObject msgJson){
//        //单机
//        byte[] msgByte = JSONObject.toJSONString(msgJson).getBytes();
//        SmartCarProtocol msg = new SmartCarProtocol(msgByte.length,msgByte);
//
//        Long fromUid = msgJson.getLong("fromUid");
//        Set<Long> rooms = uidRooms.get(fromUid);
//        for(Long room:rooms){//给本机连接的所有群的所有在线成员发消息
//            Set<Long> uids = roomIds.get(room);
//            for (Long uid:uids){
//                if(uid.equals(fromUid)){
//                    continue;
//                }
//                channels.find(cmap.get(uid)).writeAndFlush(msg);
//            }
//        }

        /**
         * 往kafka发送消息 bind-p
         * 分发服务消费消息 根据redis所有在线群里的所有人员id(和fromUid在同一台机器的不发)进行kafka分发 p2pMsg-consumer-{ip}
         */
        kafkaTemplate.send("offline-p",msgJson.toJSONString());
    }


}
