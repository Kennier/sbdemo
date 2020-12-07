package nettyServer.msgStrategy.factory;

import nettyServer.msgStrategy.BaseStrategyInterface;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StrategyFactory {
    private static Map<String, BaseStrategyInterface> map = new ConcurrentHashMap<String,BaseStrategyInterface>();

    public static BaseStrategyInterface getStrategy(String component){
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
