package nettyServer.test;

import com.leigod.modules.nettyServer.SmartCarDecoder;
import com.leigod.modules.nettyServer.SmartCarEncoder;
import com.leigod.modules.nettyServer.proto.SmartCarProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.UUID;

public class Client1003 {

    static String channelId = null;

    /**
     * 连接服务器
     *
     * @param port
     * @param host
     * @throws Exception
     */
    public void connect(int port, String host) {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture f = null;
        try {
            // 客户端辅助启动类 对客户端配置
            Bootstrap b = new Bootstrap();
            b.group(group)//
                    .channel(NioSocketChannel.class)//
                    .option(ChannelOption.TCP_NODELAY, true)//
                    .handler(new MyChannelHandler());//
            // 异步链接服务器 同步等待链接成功
            Channel channel = b.connect(host, port).sync().channel();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                int msgType = Integer.parseInt(in.readLine());
                String s = "";
                if (msgType == 3) {
                    s = "{\r\n" +
                            "     msgType : " + msgType + ",\r\n" +
                            "     msgId : '" + UUID.randomUUID() + "',\r\n" +
                            "     fromUid : " + 1003 + ",\r\n" +
                            "     toUid : " + -1 + ",\r\n" +
                            "     chatroomId : " + null + ",\r\n" +
                            "     createTime : " + Calendar.getInstance().getTimeInMillis() + ",\r\n" +
                            "     channelId : " + channelId + " \r\n" +
                            "    }";
                }
                if (msgType == 9) {
                    s = "{\r\n" +
                            "     msgType : " + msgType + ",\r\n" +
                            "     msgId : '" + UUID.randomUUID() + "',\r\n" +
                            "     fromUid : " + 1003 + ",\r\n" +
                            "     toUid : " + 1002 + ",\r\n" +
                            "     chatroomId : " + null + ",\r\n" +
                            "     createTime : " + Calendar.getInstance().getTimeInMillis() + ",\r\n" +
                            "     channelId : '" + channelId + "', \r\n" +
                            "     content : {\r\n" +
                            "                 contentType : '1',\r\n" +
                            "                 contentText : '武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发武林大会空间广阔的发',\r\n" +
//                            "                 contentText : '武林大会空间广阔的发',\r\n" +
                            "                 cmdInChatType : '0' \r\n" +
                            "                }\r\n" +
                            "    }";
                }
                if (msgType == 11) {
                    s = "{\r\n" +
                            "     msgType : " + msgType + ",\r\n" +
                            "     msgId : '" + UUID.randomUUID() + "',\r\n" +
                            "     fromUid : " + 1003 + ",\r\n" +
                            "     toUid : " + 10086l + ",\r\n" +
                            "     chatroomId : " + 10086l + ",\r\n" +
                            "     createTime : " + Calendar.getInstance().getTimeInMillis() + ",\r\n" +
                            "     channelId : '" + channelId + "', \r\n" +
                            "     content : {\r\n" +
                            "                 contentType : '1',\r\n" +
                            "                 contentText : '我是群消息呀1003',\r\n" +
//                            "                 contentText : '武林大会空间广阔的发',\r\n" +
                            "                 cmdInChatType : '0' \r\n" +
                            "                }\r\n" +
                            "    }";
                }
                // 获得要发送信息的字节数组
                byte[] content = s.getBytes();
                // 要发送信息的长度
                int contentLength = content.length;

                SmartCarProtocol protocol = new SmartCarProtocol(contentLength, content);
                Thread.sleep(1);
                channel.writeAndFlush(protocol);
            }

            // 等待链接关闭
//            f.channel().closeFuture().sync();

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            System.out.println("客户端优雅的释放了线程资源...");
        }

    }

    /**
     * 网络事件处理器
     */
    private class MyChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            // 添加自定义协议的编解码工具
            ch.pipeline().addLast(new SmartCarEncoder());
            ch.pipeline().addLast(new SmartCarDecoder());
            // 处理网络IO
            ch.pipeline().addLast(new ClientHandler());
        }

    }

    public static void main(String[] args) throws Exception {
        new Client1003().connect(8093, "127.0.0.1");
    }

}