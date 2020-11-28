package nettyServer.msgStrategy.factory;

import nettyServer.enums.MsgTypeEnum;
import nettyServer.msgStrategy.BaseStrategyInterface;
import nettyServer.msgStrategy.BindMsgStrategy;
import nettyServer.msgStrategy.ChannelMsgStrategy;
import nettyServer.msgStrategy.P2pMsgStrategy;

import java.util.HashMap;
import java.util.Map;

public class StrategyFactory {
    private static Map<Integer, BaseStrategyInterface> map = new HashMap<Integer,BaseStrategyInterface>();

    static {
        for (MsgTypeEnum e: MsgTypeEnum.values()){
            if(e.getValue() == MsgTypeEnum.P2P_MSG.getValue()){
                map.put(e.getValue(),new P2pMsgStrategy());
            }
            if(e.getValue() == MsgTypeEnum.BIND_MSG.getValue()){
                map.put(e.getValue(),new BindMsgStrategy());
            }
            if(e.getValue() == MsgTypeEnum.CHANNEL_MSG.getValue()){
                map.put(e.getValue(),new ChannelMsgStrategy());
            }
        }
    }

    public static BaseStrategyInterface getStrategy(Integer msgType){
        return map.get(msgType);
    }
}
