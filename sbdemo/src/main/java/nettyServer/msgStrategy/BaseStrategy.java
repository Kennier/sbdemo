package nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import com.leigod.modules.nettyServer.msgStrategy.factory.StrategyFactory;
import com.leigod.modules.nettyServer.proto.SmartCarProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class BaseStrategy implements com.leigod.modules.nettyServer.msgStrategy.BaseStrategyInterface {

    static ConcurrentHashMap<Long, Channel> cmap = new ConcurrentHashMap();//缓存在线uid和channelId  放redis
//    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    static ConcurrentHashMap<Long, CopyOnWriteArraySet<Long>> roomIds = new ConcurrentHashMap<>();//群id——>在线uid
    static ConcurrentHashMap<Long, CopyOnWriteArraySet<Long>> uidRooms = new ConcurrentHashMap<>();//在线uid——>群id  接口返回
    static {
        CopyOnWriteArraySet<Long> rooms = new CopyOnWriteArraySet<>();
        rooms.add(10086l);
        uidRooms.put(1001l,rooms);
        uidRooms.put(1002l,rooms);
        uidRooms.put(1003l,rooms);
    }

    public static void handleMsg(ChannelHandlerContext ctx, JSONObject msgJson) {
        int msgType = Optional.ofNullable(msgJson.getIntValue("msgType")).orElse(-1);
        if(msgType == -1){
            return;
        }
        com.leigod.modules.nettyServer.msgStrategy.BaseStrategyInterface baseStrategyInterface = StrategyFactory.getStrategy(msgType);
        if(msgType == 3){//bind
            Long fuid = msgJson.getLong("fromUid");
            cmap.put(fuid,ctx.channel());
//            if(roomIds.containsKey(msgJson.getLong("chatroomId"))){
            if(roomIds.containsKey(10086l)){
                CopyOnWriteArraySet<Long> chatroomUids = roomIds.get(10086l);
                chatroomUids.add(fuid);
            }else {
                CopyOnWriteArraySet<Long> chatroomUids = new CopyOnWriteArraySet<>();
                chatroomUids.add(fuid);
                roomIds.put(10086l,chatroomUids);
            }
            System.out.println("roomIds : "+roomIds.toString());
            System.out.println("uidRooms : "+uidRooms.toString());
            baseStrategyInterface.msgAck(ctx,msgJson);//ack时 返回客户端channelId 以后的消息都带着channelId  就不用放redis映射
        }
        if(msgType == 9 || msgType == 11 || msgType == 21){//p2p chatroom robot
            baseStrategyInterface.sendMsg(ctx,msgJson);
            //下面的考虑异步
//            baseStrategyInterface.saveMsg(ctx,msgJson);
//            baseStrategyInterface.msgAck(ctx,msgJson);
        }
        if(msgType == 5){//正常离线
            cmap.remove(msgJson.getLong("fromUid"));
            baseStrategyInterface.msgAck(ctx,msgJson);
        }
    }

    public static void removeByChannel(Channel channel){//异常退出 服务端断开连接
        for (Map.Entry<Long, Channel> entry:cmap.entrySet()){
            if(channel.equals(entry.getValue())){
                Long uid = entry.getKey();
                cmap.remove(uid);//去掉在线uid


                CopyOnWriteArraySet<Long> rooms = uidRooms.get(uid);//群id集合  接口返回
                for (Long roomId:rooms){
                    if(roomIds.containsKey(roomId)){
                        roomIds.get(roomId).remove(uid);
                    }
                }
                break;
            }
        }

    }
}