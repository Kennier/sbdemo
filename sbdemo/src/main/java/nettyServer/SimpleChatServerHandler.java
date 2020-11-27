//package com.leigod.modules.nettyServer;
//
//import com.leigod.common.system.vo.LoginUser;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.channel.group.ChannelGroup;
//import io.netty.channel.group.DefaultChannelGroup;
//import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
//import io.netty.util.concurrent.GlobalEventExecutor;
//import org.apache.shiro.SecurityUtils;
//
//import java.util.concurrent.ConcurrentHashMap;
//
//public class SimpleChatServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
//
//    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
//
//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//
//        Channel in = ctx.channel();
//        for (Channel c : channels) {
//            c.writeAndFlush("[SERVER] - " + in.remoteAddress() +":加入\n");
//        }
//
//        channels.add(in);
//    }
//
//    @Override
//    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//        Channel out = ctx.channel();
//        for (Channel c : channels) {
//            c.writeAndFlush("[SERVER] - " + out.remoteAddress() + " 离开\n");
//        }
//
//        channels.remove(out);
//    }
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame s) throws Exception {
//        String msg = s.text();
//        String userName = msg.substring(0,msg.indexOf("说:"));
//        msg = msg.substring(msg.indexOf("说:")+2);
//        Channel channel = ctx.channel();
//        for (Channel c : channels) {
//            if (channel != c) {
//                c.writeAndFlush(msgPot("{\"name\":\"" + userName + "\",\"msg\":\"" + msg + "\"}"));
//            } else {
//                c.writeAndFlush(msgPot("{\"name\":\"self\",\"msg\":\"" + msg + "\"}"));
//            }
//            System.out.println("发消息：" + msg);
//        }
//    }
//
//    //覆盖了 channelActive() 事件处理方法。服务端监听到客户端活动
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
//        Channel channel = ctx.channel();
//        System.out.println("SimpleChatClient:" + channel.remoteAddress() + "在线");
//    }
//
//    //覆盖了 channelInactive() 事件处理方法。服务端监听到客户端不活动
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
//        Channel channel = ctx.channel();
//        System.out.println("SimpleChatClient:" + channel.remoteAddress() + "掉线");
//    }
//
//    //exceptionCaught() 事件处理方法是当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在
//    // 处理事件时抛出的异常时。在大部分情况下，捕获的异常应该被记录下来并且把关联的 channel 给关闭掉。然而
//    // 这个方法的处理方式会在遇到不同异常的情况下有不同的实现，比如你可能想在关闭连接之前发送一个错误码的响应消息。
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (7)
//        Channel channel = ctx.channel();
//        System.out.println("SimpleChatClient:" + channel.remoteAddress() + "异常");
//        // 当出现异常就关闭连接
//        cause.printStackTrace();
//        ctx.close();
//    }
//
//    public TextWebSocketFrame msgPot(String msg) {
//        return new TextWebSocketFrame(msg);
//    }
//
//}
