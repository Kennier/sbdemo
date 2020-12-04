package nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import nettyServer.enums.MsgTypeEnum;
import nettyServer.proto.SmartCarProtocol;

import java.util.Optional;
import java.util.Set;

public class ChannelEnterStrategy extends BaseStrategy implements BaseStrategyInterface {

    @Override
    public void msgAck(ChannelHandlerContext ctx, JSONObject msgJson) {
        JSONObject ackMsg = new JSONObject();
        ackMsg.put("msgType", MsgTypeEnum.CHANNEL_ENTER_ACK.getValue());
        ackMsg.put("msgId", msgJson.getString("msgId"));
        ackMsg.put("fromUid", msgJson.getLong("fromUid"));
        ackMsg.put("toUid", msgJson.getLong("toUid"));
        ackMsg.put("chatroomId",msgJson.getLong("chatroomId"));
        ackMsg.put("createTime", msgJson.getLong("createTime"));
        JSONObject ackMsgContent = new JSONObject();
        ackMsgContent.put("onlineUsers", roomIds.get(msgJson.getLong("chatroomId")));
        ackMsg.put("content", ackMsgContent);
        System.out.println("发送进群ACK消息"+ackMsg.toJSONString());
        byte[] msgByte = JSONObject.toJSONString(ackMsg).getBytes();
        SmartCarProtocol msg = new SmartCarProtocol(msgByte.length,msgByte);
        ctx.writeAndFlush(msg);
    }

    @Override
    public void sendMsg(ChannelHandlerContext ctx, JSONObject msgJson){
        //通知群里所有人 有人进群（在线状态改变）
        long chatroomId = msgJson.getLong("chatroomId");
        long fromUid = msgJson.getLong("fromUid");
        //redis获取在线群成员及游客????
        Set<Long> userIds = roomIds.get(chatroomId);
        //单机版
        for (long uid: userIds){
            if(uid == fromUid){
                continue;
            }
            msgJson.put("toUid",uid);
            byte[] msgByte = JSONObject.toJSONString(msgJson).getBytes();
            SmartCarProtocol msg = new SmartCarProtocol(msgByte.length,msgByte);
            Optional<ChannelId> channelId = Optional.ofNullable(cmap.get(uid));
            if (channelId.isPresent()) {//单机版 或者就在本机
                channels.find(channelId.get()).writeAndFlush(msg);
            }
        }
        /**
         * 集群版
         * 往kafka发送消息 channelEnterMsg-p
         * 分发服务消费消息
         * 根据redis在线群id进行kafka分发(和fromUid在同一台机器的不发)到channelEnterMsg-consumer-{ip}
         */
    }
}
