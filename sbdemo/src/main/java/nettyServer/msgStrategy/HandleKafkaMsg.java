package nettyServer.msgStrategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import nettyServer.proto.SmartCarProtocol;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class HandleKafkaMsg {

    public void handleP2P(String msg){
        JSONObject msgJson = JSON.parseObject(msg);
        byte[] msgByte = JSONObject.toJSONString(msgJson).getBytes();
        SmartCarProtocol smartCarProtocol = new SmartCarProtocol(msgByte.length,msgByte);

        Long toUid = msgJson.getLong("toUid");
        BaseStrategy.channels.find(BaseStrategy.cmap.get(toUid)).writeAndFlush(smartCarProtocol);
    }

    public void handleChannelOrChannelEnter(String msg){
        JSONObject msgJson = JSON.parseObject(msg);
        byte[] msgByte = JSONObject.toJSONString(msgJson).getBytes();
        SmartCarProtocol smartCarProtocol = new SmartCarProtocol(msgByte.length,msgByte);

        long chatroomId = msgJson.getLong("chatroomId");
        Set<Long> roomOnlineUsers = BaseStrategy.roomIds.get(chatroomId);
        for(Long toUid:roomOnlineUsers){
            BaseStrategy.channels.find(BaseStrategy.cmap.get(toUid)).writeAndFlush(smartCarProtocol);
        }
    }

}
