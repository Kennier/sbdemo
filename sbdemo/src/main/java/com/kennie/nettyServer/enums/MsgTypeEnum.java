package com.kennie.nettyServer.enums;

public enum MsgTypeEnum {

    BIND_MSG("bind",3,"绑定消息类型"),
    OFFLINE_MSG("offline",5,"离线消息类型"),

    P2P_MSG("p2p",9,"私聊消息类型"),
    P2P_ACK("p2p_ack",10,"私聊消息ACK"),

    CHANNEL_MSG("channel",11,"聊天室消息类型"),
    CHANNEL_ACK("channel_ack",12,"聊天室消息ACK"),
    CHANNEL_ENTER("channelEnter",13,"进入聊天室"),
    CHANNEL_ENTER_ACK("channelEnter_ack",14,"进入聊天室ACK"),
    CHANNEL_ACTION("channelAction",15,"聊天室操作"),

    ROBOT_MSG("robot",21,"机器人消息通知"),
    ROBOT_ACK("robot_ack",22,"机器人消息ACK"),
    ;

    private final String key;
    private final Integer value;
    private String desc;

    MsgTypeEnum(String key,Integer value, String desc) {
        this.key = key;
        this.value = value;
        this.desc = desc;
    }

    /**
     * 是否包含值
     * @param value
     * @return
     */
    public static Boolean isContainValue(Integer value) {
        MsgTypeEnum msgNodeEnmu = MsgTypeEnum.matchValue(value);
        return msgNodeEnmu != null;
    }

    /**
     * 匹配值
     * @param value
     * @return
     */
    public static MsgTypeEnum matchValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (MsgTypeEnum ute: MsgTypeEnum.values()) {
            if (ute.getValue().equals(value)) {
                return ute;
            }
        }
        return null;
    }

    /**
     * 比较值
     * @param enmu
     * @param val
     * @return
     */
    public static Boolean compareVal(MsgTypeEnum enmu, Integer val){
        return enmu.getValue().equals(val);
    }

    public Integer getValue() {
        return this.value;
    }

    public String getKey() {
        return this.key;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
