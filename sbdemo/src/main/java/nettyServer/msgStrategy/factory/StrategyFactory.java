package com.leigod.modules.nettyServer.msgStrategy.factory;

import com.leigod.modules.nettyServer.enums.MsgTypeEnum;
import com.leigod.modules.nettyServer.msgStrategy.BaseStrategyInterface;
import com.leigod.modules.nettyServer.msgStrategy.BindMsgStrategy;
import com.leigod.modules.nettyServer.msgStrategy.ChannelMsgStrategy;
import com.leigod.modules.nettyServer.msgStrategy.P2pMsgStrategy;

import java.util.HashMap;
import java.util.Map;

public class StrategyFactory {
    private static Map<Integer,BaseStrategyInterface> map = new HashMap<Integer,BaseStrategyInterface>();

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
