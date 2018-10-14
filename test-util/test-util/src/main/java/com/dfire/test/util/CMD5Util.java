package com.dfire.test.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by majianfeng on 16/4/20.
 */
public class CMD5Util {
    private static final String SALT = "!#$%_d23499**(^";
    private static final String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 对md5进行再加密
     *
     * @param md5
     * @return
     */
    public static String encode(String md5) {
        return MD5Util.encode(sha256Encode(md5 + SALT));
    }

    /**
     * 将摘要信息转换为相应的编码
     *
     * @param code    编码类型
     * @param message 摘要信息
     * @return 相应的编码字符串
     */
    private static String encode(String code, String message) {
        MessageDigest md;
        String encode = null;
        try {
            md = MessageDigest.getInstance(code);
            encode = byteArrayToHexString(md.digest(message.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encode;
    }

    /**
     * 将字节数组转换为16进制的字符串
     *
     * @param byteArray 字节数组
     * @return 16进制的字符串
     */
    private static String byteArrayToHexString(byte[] byteArray) {
        StringBuffer sb = new StringBuffer();
        for (byte byt : byteArray) {
            sb.append(byteToHexString(byt));
        }
        return sb.toString();
    }

    /**
     * 将摘要信息转换成SHA-256编码
     *
     * @param message 摘要信息
     * @return SHA-256编码之后的字符串
     */
    private static String sha256Encode(String message) {
        return encode("SHA-256", message);
    }

    /**
     * 将字节转换为16进制字符串
     *
     * @param byt 字节
     * @return 16进制字符串
     */
    private static String byteToHexString(byte byt) {
        int n = byt;
        if (n < 0)
            n = 256 + n;
        return hexDigits[n / 16] + hexDigits[n % 16];
    }

}
