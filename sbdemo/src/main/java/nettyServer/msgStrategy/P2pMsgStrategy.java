package nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import com.leigod.modules.nettyServer.proto.SmartCarProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import nettyServer.msgStrategy.BaseStrategy;

import java.util.Optional;

public class P2pMsgStrategy extends BaseStrategy implements BaseStrategyInterface {

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
        byte[] msgByte = JSONObject.toJSONString(msgJson).getBytes();
        SmartCarProtocol msg = new SmartCarProtocol(msgByte.length,msgByte);
        Optional<Channel> toUid = Optional.ofNullable(BaseStrategy.cmap.get(msgJson.getLong("toUid")));
        if (toUid.isPresent()) {
            toUid.get().writeAndFlush(msg);
        }
//        BaseStrategy.channels.find(msgJson.getString("ChannelId")).writeAndFlush(msg);
    }
}
