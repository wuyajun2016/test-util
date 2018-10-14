package com.dfire.test.util;

public class StringUtil {
	/**
     * 字符串不为null or ""
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
		return null != str && !"".equals(str);
	}

    /**
     * 字符串为null or ""
     * @param str
     * @return
     */
	public static boolean isEmpty(String str) {
		return null == str || "".equals(str);
	}

	
	public static void main(String arg[]) {
	
	}
}
