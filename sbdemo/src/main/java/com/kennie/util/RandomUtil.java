package com.kennie.util;

import java.util.Random;

/**
 * 随机数工具类
 *
 * @author xuxinjian
 */
public class RandomUtil {


    public static String random(int length) {
        StringBuilder s = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            s.append(random.nextInt(10));
        }
        return s.toString();
    }


    public static int randomInt(int maxValue) {
        Random random = new Random();
        return random.nextInt(maxValue);
    }

    public static long nextLong(long maxValue) {
        Random random = new Random();
        long bits, val;
        do {
            bits = (random.nextLong() << 1) >>> 1;
            val = bits % maxValue;
        } while (bits - val + (maxValue - 1) < 0L);
        return val;
    }

}
