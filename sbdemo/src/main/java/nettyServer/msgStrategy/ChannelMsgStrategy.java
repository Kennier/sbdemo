package nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import com.leigod.modules.nettyServer.proto.SmartCarProtocol;
import io.netty.channel.ChannelHandlerContext;

import java.util.Set;

public class ChannelMsgStrategy extends BaseStrategy implements com.leigod.modules.nettyServer.msgStrategy.BaseStrategyInterface {

    @Override
    public void saveMsg(ChannelHandlerContext ctx, JSONObject msgJson) {
        System.out.println("mq发送消息");
    }

    @Override
    public void msgAck(ChannelHandlerContext ctx, JSONObject msgJson) {
        System.out.println("发送ACK消息");//到底要不要服务端ack
    }

    @Override
    public void sendMsg(ChannelHandlerContext ctx, JSONObject msgJson){
        long chatroomId = msgJson.getLong("chatroomId");
        long fromUid = msgJson.getLong("fromUid");
        //redis获取在线群成员及游客
        Set<Long> userIds = BaseStrategy.roomIds.get(chatroomId);
        for (long uid: userIds){
            if(uid == fromUid){
                continue;
            }
            byte[] msgByte = JSONObject.toJSONString(msgJson).getBytes();
            SmartCarProtocol msg = new SmartCarProtocol(msgByte.length,msgByte);
            BaseStrategy.cmap.get(uid).writeAndFlush(msg);
//            BaseStrategy.channels.find(BaseStrategy.cmap.get(uid)).writeAndFlush(msg);
        }
    }
}
