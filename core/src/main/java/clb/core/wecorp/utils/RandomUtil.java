package clb.core.wecorp.utils;

import java.util.Random;

public class RandomUtil {

    public static int getRandom(final int min, final int max)
    {
        Random rand= new Random();
        int tmp = Math.abs(rand.nextInt());
        return tmp % (max - min + 1) + min;
    }

    /*获取随机字符串*/
    public static String getNonceStr() {
        Random random = new Random();
        return MD5Util.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    public static void main(String[] args)
    {
        System.out.println(getRandom(100000,999999));
    }
}
