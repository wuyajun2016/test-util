/**
 * 百度外卖对接，签名生成工具类
 *
 * @author dongSun
 * 创建时间：2016-04-14 15:56:45
 */
package com.dfire.test.util;

import com.alibaba.fastjson.JSON;
import com.twodfire.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class BdSignUtil {

    /** 日志 */
    private Logger bizLogger = LoggerFactory.getLogger("biz");
    /** 错误日志 */
    private static final Logger errorLog = LoggerFactory.getLogger("error");

    private String appId;

    private String appSecret;

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * 根据方法参数，生成签名
     */
    public static String genSig(Map<String, String> paramsMap) {

        // 排序，拼接json字符串
        Map<String, String> sortedMap = sort(paramsMap);
        sortedMap.remove("sign");
        String jsonStr = JSON.toJSONString(sortedMap);

        // unicode编码过滤
        jsonStr = chineseToUnicode(jsonStr);

        // "{}"替换为"[]"
        jsonStr = jsonStr.replaceAll("\\{\\}", "\\[\\]");

        // 请求中的url处理
        jsonStr = jsonStr.replace("/", "\\/");

        // 计算md5
        return MD5Util.MD5(jsonStr).toUpperCase();
    }

    /**
     * 请求参数解码
     */
    public static boolean decode(Map<String, String> params, String enc) {
        if (params == null) {
            return true;
        }
        try {
            // 遍历map，key是英文的不需要转码，value需要转码
            for (Map.Entry<String, String> entry : params.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    if (value instanceof String) {
                        params.put(entry.getKey(), URLDecoder.decode((String)value, enc));
                    } else if (value instanceof Map) {
                        decode((Map<String, String>) value, enc);
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        return true;
    }

    /**
     * 对map按key的ASCⅡ升序排列
     */
    public static Map<String, String> sort(Map<String, String> params) {
        if (params == null) {
            return null;
        }

        // 对key排序
        Object[] key_arr = params.keySet().toArray();
        Arrays.sort(key_arr);

        // 重构linkedHashMap并返回，linkedHashMap可以保证put和get的顺序一致
        Map<String, String> sortedMap = new LinkedHashMap<>();
        for (Object key : key_arr) {
            sortedMap.put((String)key, params.get(key));
        }
        return sortedMap;
    }



    /**
     * 计算签名时，将字符串中非ASCⅡ字符转为unicode编码
     */
    private static String filterWithUnicode(String str) {
        if (str == null) {
            return null;
        }

        String tmp;
        char c;
        int value;
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            // 判断是否ASCⅡ字符(0-127)
            if (c <= 127) {
                sb.append(c);
                continue;
            }

            sb.append("\\u");
            // 取出高8位
            value = (c >>> 8);
            tmp = Integer.toHexString(value);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);
            // 取出低8位
            value = (c & 0xFF);
            tmp = Integer.toHexString(value);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);
        }
        return sb.toString();
    }


    /**
     * 将字符串中中文字符转为unicode编码
     */
    private static String chineseToUnicode(String str) {
        if (str == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            int chr1 = str.charAt(i);
            // 如果是中文字符
            if (chr1 >= 19968 && chr1 <= 171941) {
                sb.append("\\u").append(Integer.toHexString(chr1));
            } else {
                sb.append(str.charAt(i));
            }
        }
        return sb.toString();
    }
}
