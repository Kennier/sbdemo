package nettyServer.msgStrategy;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;

public interface BaseStrategyInterface {

    default void saveMsg(ChannelHandlerContext ctx, JSONObject msgJson){};//往mq发

    default void sendMsg(ChannelHandlerContext ctx, JSONObject msgJson){};

    void msgAck(ChannelHandlerContext ctx, JSONObject msgJson);
}
