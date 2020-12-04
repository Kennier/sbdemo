package nettyServer.testClient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import nettyServer.enums.MsgTypeEnum;
import nettyServer.proto.SmartCarProtocol;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

//用于读取客户端发来的信息
public class ClientHandler extends ChannelInboundHandlerAdapter {

    // 客户端与服务端，连接成功的售后
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        // 发送SmartCar协议的消息
        // 要发送的信息
        String data = "I am client ...  ";
        // 获得要发送信息的字节数组
        byte[] content = data.getBytes();
        // 要发送信息的长度
        int contentLength = content.length;

        SmartCarProtocol protocol = new SmartCarProtocol(contentLength, content);
//                ctx.writeAndFlush(protocol);
    }

    // 只是读数据，没有写数据的话
    // 需要自己手动的释放的消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            // 用于获取客户端发来的数据信息
            SmartCarProtocol body = (SmartCarProtocol) msg;
//            System.out.println("Client接受的客户端的信息 :" + body.toString());
            String msgBody = new String(body.getContent(), StandardCharsets.UTF_8);

            JSONObject msgJson = JSON.parseObject(msgBody);
            int msgType = Optional.ofNullable(msgJson.getIntValue("msgType")).orElse(-1);
            if(msgType == -1){
                return;
            }
            if(msgType == 3){//bind
                Client1001.channelId = msgJson.getString("channelId");
                System.out.println("Client接受的ChannelId :" + Client1001.channelId);
            }
            if(msgType == 9){//p2p
                System.out.println("Client接受的p2p信息::::::::::" + msgBody);
                JSONObject ackMsg = new JSONObject();
                ackMsg.put("msgType", MsgTypeEnum.P2P_ACK.getValue());
                ackMsg.put("msgId", msgJson.getString("msgId"));
                ackMsg.put("fromUid", msgJson.getLong("toUid"));
                ackMsg.put("toUid",msgJson.getLong("fromUid"));
                ackMsg.put("createTime", msgJson.getLong("createTime"));
                System.out.println("发送客户端ACK消息"+ackMsg.toJSONString());
                byte[] msgByte = JSONObject.toJSONString(ackMsg).getBytes();
                SmartCarProtocol ack = new SmartCarProtocol(msgByte.length,msgByte);
                ctx.writeAndFlush(ack);
            }
            if(msgType == 10){//p2p
                System.out.println("Client接受的服务端 p2pACK信息::::::::::" + msgBody);
            }
            if(msgType == 11){//群聊
                System.out.println("Client接受的群聊信息::::::::::" + msgBody);
                JSONObject ackMsg = new JSONObject();
                ackMsg.put("msgType", MsgTypeEnum.CHANNEL_ACK.getValue());
                ackMsg.put("msgId", msgJson.getString("msgId"));
                ackMsg.put("fromUid", msgJson.getLong("toUid"));
                ackMsg.put("toUid", msgJson.getLong("fromUid"));
                ackMsg.put("chatroomId",msgJson.getLong("chatroomId"));
                ackMsg.put("createTime", msgJson.getLong("createTime"));
                System.out.println("发送客户端ACK消息"+ackMsg.toJSONString());
                byte[] msgByte = JSONObject.toJSONString(ackMsg).getBytes();
                SmartCarProtocol ack = new SmartCarProtocol(msgByte.length,msgByte);
                ctx.writeAndFlush(ack);
            }
            if(msgType == 12){//群聊ACK
                System.out.println("Client接受的服务端 群聊ACK信息::::::::::" + msgBody);
            }
            if(msgType == 13){//进群
                System.out.println("Client接受的服务端 群聊信息::::::::::" + msgBody);
            }
            if(msgType == 14){//进群ACK
                System.out.println("Client接受的服务端 进群ACK信息 获取在线人员id::::::::::" + msgBody);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }


}
