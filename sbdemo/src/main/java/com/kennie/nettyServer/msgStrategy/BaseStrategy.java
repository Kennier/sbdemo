package com.kennie.nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import com.kennie.nettyServer.msgStrategy.factory.StrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public abstract class BaseStrategy implements BaseStrategyInterface {


    @Autowired
    static RedisTemplate<String, String> redisTemplate;
    /*
     *
     * 下面三个变量需要放redis集群里，netty集群的时候需要用到 （现在是单机版 垃中之垃）
     * */
    static ConcurrentHashMap<Long, CopyOnWriteArraySet<Long>> roomIds = new ConcurrentHashMap<>();//群id——>在线uid

    static ConcurrentHashMap<Long, ChannelId> cmap = new ConcurrentHashMap();//缓存本机在线uid和channelId  放redis
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);//本机存储的channel

    static ConcurrentHashMap<Long, CopyOnWriteArraySet<Long>> uidRooms = new ConcurrentHashMap<>();//在线uid——>群id  接口返回

    static CopyOnWriteArraySet<Long> rooms = new CopyOnWriteArraySet<>();
    static {
        rooms.add(10086l);
    }

    public static void handleMsg(ChannelHandlerContext ctx, JSONObject msgJson) throws UnknownHostException {
        int msgType = Optional.ofNullable(msgJson.getIntValue("msgType")).orElse(-1);
        if(msgType == -1){
            return;
        }
        BaseStrategyInterface baseStrategyInterface = StrategyFactory.getStrategy(msgType);
        Long fuid = msgJson.getLong("fromUid");
        if(msgType == 3){//bind
            cmap.put(fuid,ctx.channel().id());
            channels.add(ctx.channel());
            /**
             *
             * 存储在哪台服务器  消费kafka消息的时候，队列名称按照channelMsg-InetAddress.getLocalHost()
             * 需要处理多端登陆挤掉的消息★★★★★★★★★★★★★★
             */
            redisTemplate.opsForHash().put("online:userId:channelId",fuid, InetAddress.getLocalHost());

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
            uidRooms.put(fuid,rooms);//测试只弄一个群
            System.out.println("uidRooms : "+uidRooms.toString());
            baseStrategyInterface.msgAck(ctx,msgJson);//ack时 返回客户端channelId 以后的消息都带着channelId  就不用放redis映射
        }
        if(msgType == 9 || msgType == 11 || msgType == 13 || msgType == 21){//p2p chatroom robot
            if (msgType == 13) {
                baseStrategyInterface.msgAck(ctx,msgJson);
                baseStrategyInterface.sendMsg(ctx,msgJson);
            }
            //下面的考虑异步
            baseStrategyInterface.updateConversationAndsaveMsg(ctx,msgJson);
        }
        if(msgType == 10 || msgType == 12 || msgType == 22){//p2p chatroom robot
            //下面的考虑异步
            baseStrategyInterface.updateConversationAndsaveMsg(ctx,msgJson);
        }
        if(msgType == 5){//正常离线
            cmap.remove(fuid);
            CopyOnWriteArraySet<Long> rooms = uidRooms.get(fuid);//群id集合  接口返回
            for (Long roomId:rooms){
                if(roomIds.containsKey(roomId)){
                    roomIds.get(roomId).remove(fuid);
                }
            }
            baseStrategyInterface.msgAck(ctx,msgJson);
        }
    }

    public static void removeByChannel(Channel channel){//异常退出 服务端断开连接
        for (Map.Entry<Long, ChannelId> entry:cmap.entrySet()){
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
