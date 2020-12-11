package com.kennie.dispachServer.dispatch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Dispatch {

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public void handleP2P(String value){
        System.out.println("DS收到需要转发的私聊消息：  "+value);
        JSONObject msgJson = JSON.parseObject(value);
        Long toUid = msgJson.getLong("toUid");
        Object tochannelIp = redisTemplate.opsForHash().get("online:userId:channelIp", String.valueOf(toUid));
        tochannelIp = tochannelIp == null?"":(String)tochannelIp;
        if(!"".equals(tochannelIp)){
            kafkaTemplate.send("p2pMsg-consumer-"+tochannelIp,msgJson.toJSONString());
        }
    }

    public void handleChannelOrChannelEnter(String value){
        System.out.println("DS收到需要转发的群消息：  "+value);
        JSONObject msgJson = JSON.parseObject(value);
        Long fuid = msgJson.getLong("fromUid");
        Object fromChannelIp = redisTemplate.opsForHash().get("online:userId:channelIp", String.valueOf(fuid));
        fromChannelIp = fromChannelIp == null?"":(String)fromChannelIp;

        Long chatroomId = msgJson.getLong("chatroomId");
        //群在线的人
        Set<String> roomIds = redisTemplate.opsForSet().members("online:channel:uids"+chatroomId);
        for (String toUid : roomIds){
            msgJson.put("toUid",Long.valueOf(toUid));
            Object tochannelIp = redisTemplate.opsForHash().get("online:userId:channelIp", toUid);
            tochannelIp = tochannelIp == null?"":(String)tochannelIp;
            if(toUid.equals(String.valueOf(fuid))){// || fromChannelIp.equals(tochannelIp)表示再同一台网关机器
                continue;
            }
            if(!"".equals(tochannelIp)){
                kafkaTemplate.send("channelMsg-consumer-"+tochannelIp,msgJson.toJSONString());
            }
        }
    }

    public void handleBind(String value) {
        System.out.println("DS收到需要转发的bind消息：  "+value);
        JSONObject msgJson = JSON.parseObject(value);
        Long fuid = msgJson.getLong("fromUid");
        Object fromChannelIp = redisTemplate.opsForHash().get("online:userId:channelIp", String.valueOf(fuid));
        fromChannelIp = fromChannelIp == null?"":(String)fromChannelIp;

        /**
         * 往群里的人发消息
         */
        //人在线的群（不用接口请求）
        Set<String> rooms = redisTemplate.opsForSet().members("online:uid:channels" + fuid);
        for (String room :rooms){
            //群在线的人
            Set<String> roomIds = redisTemplate.opsForSet().members("online:channel:uids"+room);
            for (String toUid : roomIds){
                msgJson.put("toUid",Long.valueOf(toUid));
                Object tochannelIp = redisTemplate.opsForHash().get("online:userId:channelIp", toUid);
                tochannelIp = tochannelIp == null?"":(String)tochannelIp;
                if(toUid.equals(String.valueOf(fuid))){//|| fromChannelIp.equals(tochannelIp)表示再同一台网关机器
                    continue;
                }
                if(!"".equals(tochannelIp)){
                    kafkaTemplate.send("bind-consumer-"+tochannelIp,msgJson.toJSONString());
                }
            }
        }

        /**
         * 给好友发消息 可能会和群里发的重复
         */
    }
}
