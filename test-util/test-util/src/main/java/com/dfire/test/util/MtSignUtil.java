/**
 * 美团接口对接，生成和验证签名的工具类
 *
 * @author dongSun
 * 创建时间：2016-03-07 15:52:24
 */
package com.dfire.test.util;

import com.sankuai.meituan.waimai.opensdk.exception.ApiSysException;
import com.sankuai.meituan.waimai.opensdk.util.SignGenerator;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MtSignUtil {

    private String appId;
    private String appSecret;


    /**
     * 根据方法名和参数，生成签名
     */
    public String genSig(HttpServletRequest request) {

        // 获取url前缀
        String urlPrefix = getUrlPrefix(request);
        // 获取参数列表
        Map<String, String> paramsMap = getRequestParams(request);

        // 参数列表排序和拼接
        String paramStr = concatParams(paramsMap);
        String urlForGenSig = urlPrefix + "?" + paramStr + appSecret;

        try {
            return SignGenerator.genSig(urlForGenSig);
        } catch (ApiSysException e) {
            return null;
        }
    }


    /**
     * 获取不带参数的url前缀
     */
    public String getUrlPrefix(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        int index = url.indexOf("?");
        if (index == -1) {
            index = url.length();
        }

        return url.substring(0, index);
    }


    /**
     * 获取请求参数map
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> paramsMap = new HashMap<String, String>();

        Enumeration pNames = request.getParameterNames();
        while (pNames.hasMoreElements()) {
            String pName = (String) pNames.nextElement();
            String pValue = request.getParameter(pName);
            pValue = pValue == null ? "" : pValue;

            paramsMap.put(pName, pValue);
        }
        return paramsMap;
    }


    /**
     * 排序并拼接请求参数
     */
    @SuppressWarnings("unchecked")
    public static String concatParams(Map<String, String> params) {
        Object[] key_arr = params.keySet().toArray();
        Arrays.sort(key_arr);
        String str = "";

        for (Object key : key_arr) {
            if(key.equals("sig")){
                continue;
            }
            String val = params.get(key);
            if(val != null && !"".equals(val) && !"null".equals(val) && !"NULL".equals(val))
                str += "&" + key + "=" + val;
        }
        return str.replaceFirst("&", "");
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
