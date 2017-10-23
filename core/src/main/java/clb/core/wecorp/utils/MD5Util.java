package clb.core.wecorp.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by shanhd on 2016/11/2.
 */
public class MD5Util {

    public static String encode32MD5(String sourceStr){

        return encodeToMD5Buffer(sourceStr).toString();
    }

    public static String  encode16MD5(String sourceStr){
        return encodeToMD5Buffer(sourceStr).toString().substring(8, 24);
    }

    private static StringBuffer encodeToMD5Buffer(String sourceStr) {
        StringBuffer buf = new StringBuffer("");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return buf;
    }

    public static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    /***
     * MD5加密 生成32位md5码
     */
    public static String string2MD5(String str){
        if (str == null || str.length() == 0) {
            return null;
        }
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };

        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf).toUpperCase();
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer("20161103115015,0");
        System.out.println("对123456用MD5加密后：" + sb.toString().split("\n")[0].split(",")[1]);
        String password = MD5Util.encode32MD5("qwe.1234567");
        System.out.println("对123456用MD5加密后：" + password);
        System.out.println(MD5Util.encode32MD5("bjgasweixinzhifu").length());

        System.out.println(string2MD5("appid=wx1038ea2c67b7a4a1&body=燃气灶&input_charset=UTF-8&mch_id=1434357802&nonce_str=0e7c7d6c41c76b9ee6445ae01cc0181d&notify_url=http://zttest.bjgas.com/bjgas-server/c/api/wxCallback&out_trade_no=BG1484556062552&spbill_create_ip=127.0.0.1&total_fee=88&trade_type=APP&key=d65b4818b2143fd00872d441b4d0e4e5"));
    }
}
