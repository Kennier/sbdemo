package nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import nettyServer.enums.MsgTypeEnum;
import nettyServer.proto.SmartCarProtocol;

import java.util.Optional;

/**
 * 接收客户端ACK
 *
 *
 */
public class P2pMsgAckStrategy extends BaseStrategy implements BaseStrategyInterface {

    @Override
    public void updateConversationAndsaveMsg(ChannelHandlerContext ctx, JSONObject msgJson) {
        System.out.println("mq发送消息：做消息已读");
        /**
         *
         * 保存每条未读的消息id
         *
         *
         */
    }

}
