package com.kennie.nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 接收客户端ACK
 *
 *
 */
@Component("p2p_ack")
public class P2pMsgAckStrategy extends BaseStrategy implements BaseStrategyInterface {

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Override
    public void updateConversationAndsaveMsg(ChannelHandlerContext ctx, JSONObject msgJson) {
        System.out.println("mq发送消息：做消息已读");
//        kafkaTemplate.send("p2pMsg-ack-p",msgJson.toJSONString());
        /**
         *
         * 发送已读消息到kafka p2pMsg-ack-p
         * 存储服务消费消息并更新未读数（或者是更新最后一条已读）
         *
         */
    }

}
