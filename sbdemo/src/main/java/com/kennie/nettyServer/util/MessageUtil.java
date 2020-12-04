package com.leigod.modules.nettyServer.util;

import java.util.UUID;


public class MessageUtil {

    /**
     * 随机生成id
     */
    public static int randomId(){
        return (int)(Math.random()*(9999-1000+1)+1000);
    }


    /**
     * byte转int类型
     * 如果byte是负数，则转出的int型是正数
     * @param b
     * @return
     */
    public static int byteToInt(byte b){
        int x = b & 0xff;
        return x;
    }

    /**
     * byte数组转换为整数
     * 第0个byte与上0xff,生成整数,在右移24位，取得一个整数
     * 第1个byte与上0xff,生成整数,在右移16位，取得一个整数
     * 第2个byte与上0xff,生成整数,在右移8位，取得一个整数
     * 第3个byte与上0xff,生成整数
     * 把四个整数做或操作,转换为已整数
     */
    public static int byteArrToInt(byte[] arr){
        int x = ((arr[0] & 0xff) << 24 )|((arr[1]& 0xff) <<16 )|((arr[2] & 0xff)<<8)|(arr[3] & 0xff);
        return x;
    }

    public static byte[] intToBytes(int a, int length) {
        byte[] bs = new byte[length];
        for (int i = bs.length - 1; i >= 0; i--) {
            bs[i] = (byte) (a % 255);
            a = a / 255;
        }
        return bs;
    }

    public static byte[] int2Bytes(int num) {
        byte[] bytes = new byte[4];
        //通过移位运算，截取低8位的方式，将int保存到byte数组
        bytes[0] = (byte)(num >>> 24);
        bytes[1] = (byte)(num >>> 16);
        bytes[2] = (byte)(num >>> 8);
        bytes[3] = (byte)num;
        return bytes;
    }

    public static short byte2short(byte[] b){
        short l = 0;
        for (int i = 0; i < 2; i++) {
            l<<=8; //<<=和我们的 +=是一样的，意思就是 l = l << 8
            l |= (b[i] & 0xff); //和上面也是一样的  l = l | (b[i]&0xff)
        }
        return l;
    }

    public static byte[] short2ByteNew(short x){
        byte high = (byte) (0x00FF & (x>>8));//定义第一个byte
        byte low = (byte) (0x00FF & x);//定义第二个byte
        byte[] bytes = new byte[2];
        bytes[0] = high;
        bytes[1] = low;
        return bytes;
    }

    public static char byteToChar(byte[] b) {
        char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
        return c;
    }


    public static char intToChar(int i) {
        String s = String.valueOf(i);
        char c = s.charAt(0);
        return c;
    }

    public static byte[] long2Bytes(Long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    public static long bytes2Long(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }
    public static long bytes2Long1(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < byteNum.length; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }

    /**
     * 合并byte[]数组 （不改变原数组）
     * @param byte_1
     * @param byte_2
     * @return 合并后的数组
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }


    // 生成UUID
    public static String getUuid(){
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

}
