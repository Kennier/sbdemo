package com.kennie.nettyServer.msgStrategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kennie.nettyServer.proto.SmartCarProtocol;
import io.netty.channel.ChannelId;
import org.springframework.stereotype.Component;

@Component
public class HandleKafkaMsg {

    public void handleP2P(String msg){
        System.out.println("需要处理收到的私聊消息：   "+msg);
        JSONObject msgJson = JSON.parseObject(msg);
        byte[] msgByte = JSONObject.toJSONString(msgJson).getBytes();
        SmartCarProtocol smartCarProtocol = new SmartCarProtocol(msgByte.length,msgByte);

        Long toUid = msgJson.getLong("toUid");
        ChannelId channelId = BaseStrategy.cmap.get(toUid);
        if(channelId != null){
            BaseStrategy.channels.find(channelId).writeAndFlush(smartCarProtocol);
        }
    }

    public void handleChannelOrChannelEnter(String msg){
        System.out.println("需要处理收到的群消息：   "+msg);
        JSONObject msgJson = JSON.parseObject(msg);
        byte[] msgByte = JSONObject.toJSONString(msgJson).getBytes();
        SmartCarProtocol smartCarProtocol = new SmartCarProtocol(msgByte.length,msgByte);

        long toUid = msgJson.getLong("toUid");
        ChannelId channelId = BaseStrategy.cmap.get(toUid);
        if(channelId != null) {
            BaseStrategy.channels.find(BaseStrategy.cmap.get(toUid)).writeAndFlush(smartCarProtocol);
        }
    }

    public void handleBind(String msg) {
        System.out.println("需要处理收到的bind消息：   "+msg);
        JSONObject msgJson = JSON.parseObject(msg);
        byte[] msgByte = JSONObject.toJSONString(msgJson).getBytes();
        SmartCarProtocol smartCarProtocol = new SmartCarProtocol(msgByte.length,msgByte);

        Long toUid = msgJson.getLong("toUid");
        ChannelId channelId = BaseStrategy.cmap.get(toUid);
        if(channelId != null){
            BaseStrategy.channels.find(channelId).writeAndFlush(smartCarProtocol);
        }
    }
}
