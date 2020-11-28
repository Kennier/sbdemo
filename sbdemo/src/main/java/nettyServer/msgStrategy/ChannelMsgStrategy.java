package nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import nettyServer.enums.MsgTypeEnum;
import nettyServer.proto.SmartCarProtocol;

import java.util.Optional;
import java.util.Set;

public class ChannelMsgStrategy extends BaseStrategy implements BaseStrategyInterface {

    @Override
    public void saveMsg(ChannelHandlerContext ctx, JSONObject msgJson) {
        System.out.println("mq发送消息");
    }

    @Override
    public void msgAck(ChannelHandlerContext ctx, JSONObject msgJson) {
        JSONObject ackMsg = new JSONObject();
        ackMsg.put("msgType", MsgTypeEnum.CHANNEL_ACK.getValue());
        ackMsg.put("msgId", msgJson.getString("msgId"));
        ackMsg.put("fromUid", msgJson.getLong("fromUid"));
        ackMsg.put("chatroomId",msgJson.getLong("chatroomId"));
        ackMsg.put("createTime", msgJson.getLong("createTime"));
        System.out.println("发送ACK消息"+ackMsg.toJSONString());
        byte[] msgByte = JSONObject.toJSONString(ackMsg).getBytes();
        SmartCarProtocol msg = new SmartCarProtocol(msgByte.length,msgByte);
        ctx.writeAndFlush(msg);
    }

    @Override
    public void sendMsg(ChannelHandlerContext ctx, JSONObject msgJson){
        long chatroomId = msgJson.getLong("chatroomId");
        long fromUid = msgJson.getLong("fromUid");
        //redis获取在线群成员及游客????
        Set<Long> userIds = BaseStrategy.roomIds.get(chatroomId);
        for (long uid: userIds){
            if(uid == fromUid){
                continue;
            }
            byte[] msgByte = JSONObject.toJSONString(msgJson).getBytes();
            SmartCarProtocol msg = new SmartCarProtocol(msgByte.length,msgByte);
            Optional<Channel> toUid = Optional.ofNullable(BaseStrategy.cmap.get(uid));
            if (toUid.isPresent()) {
                toUid.get().writeAndFlush(msg);
            }
//            BaseStrategy.channels.find(BaseStrategy.cmap.get(uid)).writeAndFlush(msg);
        }
    }
}
