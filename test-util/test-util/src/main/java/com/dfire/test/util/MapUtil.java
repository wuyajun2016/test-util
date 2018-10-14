/**
 * Map工具类
 *
 * @author dongSun
 * 创建时间：2016-04-14 10:03:47
 */
package com.dfire.test.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

public class MapUtil {

    /** 错误日志 */
    private static final Logger errorLog = LoggerFactory.getLogger("error");

    /**
     * 针对http请求的多值map，获取单一的属性值
     * 取第一个值的时候，index传0
     */
    public static String getSingleValue(Map<String, String[]> params, String key, int index) {
        if (params == null) {
            return null;
        }

        String[] valueArr = params.get(key);
        if (valueArr == null || index < 0 || index >= valueArr.length) {
            return null;
        }
        return valueArr[index];
    }


    /**
     * 对map按key的ASCⅡ升序排列
     */
    public static Map<String, String[]> sort(Map<String, String[]> params) {
        if (params == null) {
            return null;
        }

        // 对key排序
        Object[] key_arr = params.keySet().toArray();
        Arrays.sort(key_arr);

        // 重构linkedHashMap并返回，linkedHashMap可以保证put和get的顺序一致
        Map<String, String[]> sortedMap = new LinkedHashMap<>();
        for (Object key : key_arr) {
            sortedMap.put((String)key, params.get(key));
        }
        return sortedMap;
    }


    /**
     * 请求参数编码
     */
    public static boolean encode(Map<String, String> params, String enc)  {
        if (params == null) {
            return true;
        }

        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value != null) {
                    params.put(key, URLEncoder.encode(value, enc));
                }
            }
        } catch (UnsupportedEncodingException e) {
            errorLog.error("unsupported encode strategy: {}, params: {}", enc, params);
            return false;
        }
        return true;
    }


    /**
     * 请求参数解码
     */
    public static boolean decode(Map<String, String[]> params, String enc) {
        if (params == null) {
            return true;
        }

        try {
            // 遍历map，key是英文的不需要转码，value需要转码
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String[] valueArr = entry.getValue();
                if (valueArr != null) {
                    for (int i=0; i < valueArr.length; i++) {
                        if (valueArr[i] == null) {
                            continue;
                        }
                        valueArr[i] = URLDecoder.decode(valueArr[i], enc);
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            errorLog.error("unsupported decode strategy: {}, params: {}", enc, params);
            return false;
        }
        return true;
    }


    /**
     * 按照key的ASCⅡ升序连接所有请求参数
     * get请求/计算签名时使用
     */
    public static String connectAsString(Map<String, String[]> params) {
        if (params == null) {
            return null;
        }

        Object[] key_arr = params.keySet().toArray();
        Arrays.sort(key_arr);
        StringBuilder sb = new StringBuilder(64);

        for (Object key : key_arr) {
            String[] valArr = params.get(key);
            if (valArr == null) {
                sb.append("&").append(key).append("=");
            } else {
                for (String val : valArr) {
                    sb.append("&").append(key).append("=").append(val);
                }
            }
        }
        // 去掉第一个"&"
        return sb.substring(1);
    }


    /**
     * 根据单值参数map，生成多值参数map
     */
    public static Map<String, String[]> generateMultiValueMap(Map<String, String> params) {
        if (params == null) {
            return null;
        }

        Map<String, String[]> resultMap = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null) {
                resultMap.put(key, null);
            } else {
                resultMap.put(key, new String[]{value});
            }
        }
        return resultMap;
    }
}
