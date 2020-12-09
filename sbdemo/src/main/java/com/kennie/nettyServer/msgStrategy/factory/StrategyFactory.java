package com.kennie.nettyServer.msgStrategy.factory;

import com.kennie.nettyServer.msgStrategy.BaseStrategyInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StrategyFactory {

    @Autowired
    private Map<String, BaseStrategyInterface> map = new ConcurrentHashMap<>();

    public BaseStrategyInterface getStrategy(String component){
        BaseStrategyInterface strategy = map.get(component);
        if(strategy == null) {
            throw new RuntimeException("no strategy defined");
        }
        return strategy;
    }

//    static {
//        for (MsgTypeEnum e: MsgTypeEnum.values()){
//            if(e.getValue() == MsgTypeEnum.BIND_MSG.getValue()){
//                map.put(e.getValue(),new BindMsgStrategy());
//            }
//            if(e.getValue() == MsgTypeEnum.P2P_MSG.getValue()){
//                map.put(e.getValue(),new P2pMsgStrategy());
//            }
//            if(e.getValue() == MsgTypeEnum.P2P_ACK.getValue()){
//                map.put(e.getValue(),new P2pMsgAckStrategy());
//            }
//            if(e.getValue() == MsgTypeEnum.CHANNEL_MSG.getValue()){
//                map.put(e.getValue(),new ChannelMsgStrategy());
//            }
//            if(e.getValue() == MsgTypeEnum.CHANNEL_ACK.getValue()){
//                map.put(e.getValue(),new ChannelMsgAckStrategy());
//            }
//            if(e.getValue() == MsgTypeEnum.CHANNEL_ENTER.getValue()){
//                map.put(e.getValue(),new ChannelEnterStrategy());
//            }
//        }
//    }
//
//    public static BaseStrategyInterface getStrategy(Integer msgType){
//        return map.get(msgType);
//    }
}
