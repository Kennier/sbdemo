package com.kennie.nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import com.kennie.nettyServer.enums.MsgTypeEnum;
import com.kennie.nettyServer.msgStrategy.factory.StrategyFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component("baseStrategy")
public class BaseStrategy implements BaseStrategyInterface {

    @Autowired
    StrategyFactory strategyFactory;

    @Autowired
    RedisTemplate<String, String> redisTemplate;
    /*
     *
     * 下面三个变量需要放redis集群里，netty集群的时候需要用到 （现在是单机版 垃中之垃）
     * */

    static ConcurrentHashMap<Long, ChannelId> cmap = new ConcurrentHashMap<Long, ChannelId>();//缓存本机在线uid和channelId  放redis
    static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);//本机存储的channel

    static ConcurrentHashMap<Long, CopyOnWriteArraySet<Long>> roomIds = new ConcurrentHashMap<>();//群id——>在线uid（连接在本机的uid）
    static ConcurrentHashMap<Long, CopyOnWriteArraySet<Long>> uidRooms = new ConcurrentHashMap<>();//在线uid——>群id  接口返回

    private static CopyOnWriteArraySet<Long> rooms = new CopyOnWriteArraySet<>();
    static {
        rooms.add(10086l);
//        rooms.add(10000l);
//        rooms.add(10010l);
    }

    public void handleMsg(ChannelHandlerContext ctx, JSONObject msgJson) throws UnknownHostException {
        int msgType = Optional.of(msgJson.getIntValue("msgType")).orElse(-1);
        if(msgType == -1){
            return;
        }
//        BaseStrategyInterface baseStrategyInterface = strategyFactory.getStrategy(MsgTypeEnum.matchValue(msgType).getKey());
        BaseStrategyInterface baseStrategyInterface = strategyFactory.getStrategy(MsgTypeEnum.matchValue(msgType).getKey());
        Long fuid = msgJson.getLong("fromUid");
        if(msgType == MsgTypeEnum.BIND_MSG.getValue()){//bind
            cmap.put(fuid,ctx.channel().id());
            channels.add(ctx.channel());
            /**
             *
             * 存储在哪台服务器  消费kafka消息的时候，队列名称按照channelMsg-InetAddress.getLocalHost()
             * 需要处理多端登陆挤掉的消息★★★★★★★★★★★★★★
             */
//            redisTemplate.opsForHash().put("online:userId:channelIp",String.valueOf(fuid), InetAddress.getLocalHost());
            redisTemplate.opsForHash().put("online:userId:channelIp",String.valueOf(fuid), "netty1");

            //获取人所在的群  处理加群机器人消息的时候需要变动这两个属性★★★★★★★★★★★★★★
            updateRoomsAndIds(fuid);

            System.out.println("roomIds : "+roomIds.toString());
            uidRooms.put(fuid,rooms);//测试只弄一个群
            System.out.println("uidRooms : "+uidRooms.toString());
            baseStrategyInterface.msgAck(ctx,msgJson);//ack时 返回客户端channelId 以后的消息都带着channelId  就不用放redis映射

            baseStrategyInterface.sendMsg(ctx,msgJson);//还是要推给群里所有在线的人（老子上线了）
        }
        if(msgType == MsgTypeEnum.P2P_MSG.getValue() || msgType == MsgTypeEnum.CHANNEL_MSG.getValue() || msgType == MsgTypeEnum.CHANNEL_ENTER.getValue() || msgType == MsgTypeEnum.ROBOT_MSG.getValue()){//p2p chatroom chatroomAction robot
            baseStrategyInterface.msgAck(ctx,msgJson);
            baseStrategyInterface.sendMsg(ctx,msgJson);
            //只有游客会有进群动作 成员上线就进群了
            if (msgType != MsgTypeEnum.CHANNEL_ENTER.getValue()) {
                //下面的考虑异步
                baseStrategyInterface.updateConversationAndsaveMsg(ctx,msgJson);
            }else {
                //更新游客的  在线群和在线人 的关系
                Long chatroomId = msgJson.getLong("chatroomId");

                CopyOnWriteArraySet<Long> fromUididRooms = uidRooms.get(fuid);
                fromUididRooms.add(chatroomId);
                redisTemplate.opsForSet().add("online:channel:uids"+chatroomId, String.valueOf(fuid));//群在线的人

                CopyOnWriteArraySet<Long> roomUids = roomIds.get(chatroomId);
                roomUids.add(fuid);
                roomIds.put(chatroomId, roomUids);
                redisTemplate.opsForSet().add("online:uid:channels"+fuid, String.valueOf(chatroomId));//人在线的群
            }
        }
        if(msgType == MsgTypeEnum.P2P_ACK.getValue() || msgType == MsgTypeEnum.CHANNEL_ACK.getValue() || msgType == MsgTypeEnum.ROBOT_ACK.getValue()){//p2p chatroom robot
            //下面的考虑异步
            baseStrategyInterface.updateConversationAndsaveMsg(ctx,msgJson);
        }
        if(msgType == MsgTypeEnum.OFFLINE_MSG.getValue()){//正常离线
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

    public void updateRoomsAndIds(Long fuid){
        //获取人所在的群  处理加群机器人消息的时候需要变动这两个属性★★★★★★★★★★★★★★
        for (Long room: rooms){//room接口返回
            if(roomIds.containsKey(room)){
                CopyOnWriteArraySet<Long> chatroomUids = roomIds.get(room);
                chatroomUids.add(fuid);
            }else {
                CopyOnWriteArraySet<Long> chatroomUids = new CopyOnWriteArraySet<>();
                chatroomUids.add(fuid);
                roomIds.put(room,chatroomUids);
            }
            redisTemplate.opsForSet().add("online:channel:uids"+room, String.valueOf(fuid));//群在线的人
            redisTemplate.opsForSet().add("online:uid:channels"+fuid, String.valueOf(room));//人在线的群
        }
    }
}
