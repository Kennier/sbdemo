package nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component("channel_ack")
public class ChannelMsgAckStrategy extends BaseStrategy implements BaseStrategyInterface {

    @Override
    public void updateConversationAndsaveMsg(ChannelHandlerContext ctx, JSONObject msgJson) {
        System.out.println("mq发送消息：消息已读");
        /**
         *
         * 发送已读消息到kafka channelMsg-p-ack
         * 存储服务消费消息并更新未读数（或者是更新最后一条已读）
         *
         */
    }

}
