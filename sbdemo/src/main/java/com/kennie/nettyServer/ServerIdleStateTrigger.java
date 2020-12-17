package com.kennie.nettyServer;

import com.kennie.nettyServer.msgStrategy.BaseStrategy;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * <p>在规定时间内未收到客户端的任何数据包, 将主动断开该连接</p>
 */
public class ServerIdleStateTrigger extends ChannelInboundHandlerAdapter {

    @Autowired
    @Qualifier("baseStrategy")
    BaseStrategy baseStrategy;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                // 在规定时间内没有收到客户端的上行数据, 主动断开连接
                ctx.disconnect();
                baseStrategy.removeByChannel(ctx.channel());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
