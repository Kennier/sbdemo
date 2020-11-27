package com.leigod.modules.nettyServer.enums;

public enum MsgTypeEnum {

    BIND_MSG(3,"绑定消息类型"),
    P2P_MSG(9,"私聊消息类型"),
    CHANNEL_MSG(11,"聊天室消息类型"),
    ROBOT_MSG(21,"机器人消息通知"),
    ;

    private final Integer value;
    private String desc;

    MsgTypeEnum(Integer value, String desc) {
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

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
